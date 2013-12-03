package models.dao

import play.api.db.slick.Profile
import org.joda.time.DateTime

case class ArticleRecord(id: Option[Long], title: String, summary: String, text: String, imageId: Long, addedAt: DateTime)

trait ArticleComponent { this: Profile with ImageComponent =>
  import profile.simple._
  import com.github.tototoshi.slick.JodaSupport._

  def Images: Images
  
  class Articles extends Table[ArticleRecord]("articles") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title", O.NotNull)
    def summary = column[String]("summary", O.NotNull, O.DBType("text"))
    def text = column[String]("text", O.NotNull, O.DBType("text"))
    def imageId = column[Long]("image_id", O.NotNull)
    def addedAt = column[DateTime]("added_at", O.NotNull)
    
    def image = foreignKey("Articles_ImageFK", imageId, Images)(_.id)
    
    def * = id.? ~ title ~ summary ~ text ~ imageId ~ addedAt <> (ArticleRecord, ArticleRecord.unapply _)
  
    def autoInc = * returning id
  }
}