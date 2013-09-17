package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp

case class Article(title: String, summary: String, text: String, imageId: Long, addedAtRaw: Timestamp, id: Option[Long] = None) extends ModelEntity[Article] {
  val addedAt: DateTime = new DateTime(addedAtRaw.getTime())
  
  def withId(newId: Option[Long]) = copy(id = newId)
}

object Articles extends Table[Article]("articles") with CrudSupport[Article] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def summary = column[String]("summary", O.NotNull, O.DBType("text"))
  def text = column[String]("text", O.NotNull, O.DBType("text"))
  def imageId = column[Long]("image_id", O.NotNull)
  def addedAt = column[Timestamp]("added_at", O.NotNull)
  
  def image = foreignKey("Articles_ImageFK", imageId, Images)(_.id)
  
  def * = title ~ summary ~ text ~ imageId ~ addedAt ~ id.? <> (Article, Article.unapply _)
  
  def findAll(implicit session: Session) = Query(Articles).list
}