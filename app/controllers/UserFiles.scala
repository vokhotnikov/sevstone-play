package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current
import java.io.FileNotFoundException

trait UserFilesController extends Controller {
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

  def file(path: String) = Action {
    val filePath = s"/Users/hunter/work/sevstone-old-files/$path";
    val file = new java.io.File(filesRoot, path)
    if (! file.isFile() || file.isHidden()) {
      println(s"Requested file not found: ${file.getAbsolutePath}")
      NotFound
    } else if (! file.canRead()) {
      println(s"Requested file is forbidden: ${file.getAbsolutePath}")
      Forbidden
    } else {
      Ok.sendFile(file)
    }
  }
}

object UserFilesController extends UserFilesController