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
      }
    }
  }

  trait TestData extends ArticlesComponent with ImagesComponent {
    val service = new ArticleService
    val ImageService = new ImageService
    
    //val a1 = new Article("Urgent news!", "Read this", "not so urgent after all", DateTime.now, true)
    
  }
}