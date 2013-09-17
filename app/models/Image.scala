package models

import play.api.db.slick.Config.driver.simple._
import java.sql.Timestamp
import org.joda.time.DateTime

case class Image(url: String, addedAtRaw: Timestamp, id: Option[Long] = None) extends ModelEntity[Image] {
  val addedAt = new DateTime(addedAtRaw.getTime())
  
  def withId(newId: Option[Long]) = copy(id = newId)
}

object Images extends Table[Image]("images") with CrudSupport[Image] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def url = column[String]("url", O.NotNull)
  def addedAt = column[Timestamp]("added_at", O.NotNull)
  
  def * = url ~ addedAt ~ id.? <> (Image, Image.unapply _)
  
  def findAll(implicit session: Session) = Query(Images).list
}