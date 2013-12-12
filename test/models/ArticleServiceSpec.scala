package models

import org.specs2.mutable._
import models.dao.current.daoService
import org.specs2.specification.Scope
import com.github.nscala_time.time.Imports._

import daoService.profile.simple._

import play.api.db.slick.DB

class ArticleServiceSpec extends Specification {
  "article service" should {
    "return latest articles" in new WithTestDb with TestData {
      DB.withSession { implicit s: Session =>
        service.latestArticles(10) must be empty

        val (a1, a2, a3) = makeTestArticles
        
        val id1 = service add a1
        val id3 = service add a3

        service.latestArticles(2) must_== List(Loaded(id1, a1), Loaded(id3, a3))
        service.latestArticles(3) must_== List(Loaded(id1, a1), Loaded(id3, a3))

        val id2 = service add a2

        service.latestArticles(2) must_== List(Loaded(id1, a1), Loaded(id2, a2))
        service.latestArticles(3) must_== List(Loaded(id1, a1), Loaded(id2, a2), Loaded(id3, a3))
      }
    }
  }

  trait TestData extends ArticlesComponent with ImagesComponent {
    val ArticleService = new ArticleService
    val ImageService = new ImageService

    val service = ArticleService
    
    def makeTestArticles(implicit session: Session) = { 
      val i1 = ImageService.findById(ImageService.add(Image("http://myhost.ex/images/tesa123.jpg", DateTime.now - 3.months))).get
      val i2 = ImageService.findById(ImageService.add(Image("file:///tmp/TT.PNG", DateTime.now - 7.weeks))).get

      (Article("Urgent news!", "Read this", "not so urgent after all", i1, DateTime.now),
        Article("Test article", "", "some test article here", i2, DateTime.now - 2.months),
        Article("Other news", "Some important news", "very-very important", i1, DateTime.now - 10.weeks))
    }
  }
}