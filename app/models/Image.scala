package models

import play.api.db.slick.Config.driver.simple._

case class Image(url: String, id: Option[Long] = None) extends ModelEntity[Image] {
  def withId(newId: Option[Long]) = copy(id = newId)
}

object Images extends Table[Image]("images") with CrudSupport[Image] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def url = column[String]("url", O.NotNull)
  
  def * = url ~ id.? <> (Image, Image.unapply _)
  
  def findAll(implicit session: Session) = Query(Images).list
}