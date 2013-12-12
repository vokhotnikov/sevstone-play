package models

import play.api.db.slick._
import org.joda.time.DateTime
import models.dao.ArticleRecord

case class Article(title: String, summary: String, text: String, image: MaybeLoaded[Image], addedAt: DateTime)

trait ArticlesComponent { this: ImagesComponent => 
  val ArticleService: ArticleService
  
  class ArticleService extends BasicServiceOps[Article] {
    object daoMapping {
      import scala.language.implicitConversions
      
      implicit def mapToLoaded(v: ArticleRecord)(implicit session: Session) =
        Loaded(v.id.get, Article(v.title, v.summary, v.text, ImageService.findById(v.imageId).get, v.addedAt))
      implicit def mapToRecord(a: Article)(implicit session: Session) = 
        ArticleRecord(None, a.title, a.summary, a.text, ImageService.addIfNeeded(a.image), a.addedAt)
    }
    
    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    private val byId = daoService.Articles.createFinderBy(_.id)
    
    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)
      
    def add(article: Article)(implicit session: Session) = daoService.Articles.autoInc insert article
    
    def latestArticles(limit: Int)(implicit session: Session) = 
      Query(daoService.Articles).sortBy(_.addedAt.desc).take(limit).list.map(mapToLoaded)
  }
}