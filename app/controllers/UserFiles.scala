package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current
import java.io.FileNotFoundException
import java.io.File
import models.dao.ImageRecord

trait UserFilesController extends Controller { this: ModelServicesComponent =>
  lazy val filesRoot = {
    val path = current.configuration.getString("sevstone.files-root").getOrElse {
      throw new NoSuchElementException("sevstone.files-root value not found in the config")
    }
    val dir = new java.io.File(path)
    if (!(dir.isDirectory && dir.canRead())) {
      throw new FileNotFoundException(s"Files root dir ${dir.getAbsolutePath()} is not an exisitng readable directory");
    }
    dir
  }

  lazy val userfilesNormalizedMap = {
    /**
     * Get a recursive listing of all files underneath the given directory.
     * from stackoverflow.com/questions/2637643/how-do-i-list-all-files-in-a-subdirectory-in-scala
     */
    def getRecursiveListOfFiles(dir: File): Array[File] = {
      val these = dir.listFiles
      these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
    }
    
    val files = getRecursiveListOfFiles(filesRoot).filter(f => f.isFile() && f.canRead())
    val result = files.map(f => f.getAbsolutePath().toLowerCase() -> f).toMap
    
    println(s"Indexed ${result.size} files under ${filesRoot.getAbsolutePath()}")
    result.take(10).map(_._1).foreach(println(_))
    
    result
  }

  def file(path: String) = Action {
    val normalizedPath = path.toLowerCase()
    val filePath = s"/Users/hunter/work/sevstone-old-files/$normalizedPath";
    val file = new java.io.File(filesRoot, normalizedPath)
    if (!file.isFile() || file.isHidden()) {
      println(s"Requested file not found: ${file.getAbsolutePath}")
      NotFound
    } else if (!file.canRead()) {
      println(s"Requested file is forbidden: ${file.getAbsolutePath}")
      Forbidden
    } else {
      Ok.sendFile(file)
    }
  }

  def exportAll = DBAction { implicit r =>
    import models.dao.current.daoService
    import models.dao.current.daoService.profile.simple._

    val toImport = Query(daoService.Images).filter(_.fileUID.isNull).list

    val processed = toImport.flatMap { img =>
      val path = if (img.url.startsWith("userfiles/")) img.url.substring("userfiles/".length) else img.url
      val filePath = new java.io.File(filesRoot, path).getAbsolutePath()
      
      userfilesNormalizedMap.get(filePath.toLowerCase()).map { file => 
        val uid = se.digiplant.res.Res.put(file)
        val up = for {
          i <- daoService.Images if i.id === img.id
        } yield i.fileUID

        up.update(Some(uid))
        
        play.api.libs.Files.copyFile(se.digiplant.res.Res.get(uid), new File(filePath), true, true)
      }
    }

    Ok(s"All done, ${processed.size} new files imported")
  }
  
  def exportArticleImages = DBAction { implicit r =>
    import models.dao.current.daoService
    import models.dao.current.daoService.profile.simple._

    
    val processed = Services.ArticleService.allArticles.flatMap { la =>
      val text = la.value.text;
      val toSubstitute = """(?i)<img[^>]*? src="([^"]+?)"[^>]*>""".r.findAllIn(text).matchData.map { m =>
        (la, m.group(1).trim)
      }.flatMap { case (la, url) => 
        if (url.startsWith("/userfiles/")) Some((la, url.substring("/userfiles/".length), url)) else None
      }.flatMap { case (la, url, originalUrl) =>
        val filePath = new java.io.File(filesRoot, url).getAbsolutePath()
        
        userfilesNormalizedMap.get(filePath.toLowerCase()).map { file => 
          val uid = se.digiplant.res.Res.put(file)
          play.api.libs.Files.copyFile(se.digiplant.res.Res.get(uid), new File(filePath), true, true)
          daoService.Images.autoInc.insert((url, la.value.addedAt, Some(uid)))
          (originalUrl, uid)
        }
      }.toMap
      
      val newText = """(?i) src="([^"]+?)"""".r.replaceAllIn(la.value.text, m => {
        toSubstitute.get(m.group(1)).map(r => " src=\"/file/" + r + "\"").getOrElse(m.matched)
      })
      
      val up = for {
        a <- daoService.Articles if a.id === la.id
      } yield a.text
     
      up.update(newText)
      
      toSubstitute
    }
    
    Ok(s"All done, ${processed.size} images imported")
  }
}

object UserFilesController extends UserFilesController with ModelServicesComponent