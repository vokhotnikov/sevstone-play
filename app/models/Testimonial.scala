package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp

case class Testimonial(authorName: String, authorEmail: Option[String], text: String, addedAtRaw: Timestamp, isApproved: Boolean, id: Option[Long] = None) extends ModelEntity[Testimonial] {
  val addedAt: DateTime = new DateTime(addedAtRaw.getTime())

  def withId(newId: Option[Long]) = copy(id = newId)
}

object Testimonials extends Table[Testimonial]("testimonials") with CrudSupport[Testimonial] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def authorName = column[String]("author_name", O.NotNull)
  def authorEmail = column[Option[String]]("author_email")
  def text = column[String]("text", O.NotNull, O.DBType("text"))
  def addedAt = column[Timestamp]("added_at", O.NotNull)
  def isApproved = column[Boolean]("is_approved", O.NotNull)
  
  def * = authorName ~ authorEmail ~ text ~ addedAt ~ isApproved ~ id.? <> (Testimonial, Testimonial.unapply _)
  
  def findAll(implicit session: Session) = Query(Testimonials).list
}