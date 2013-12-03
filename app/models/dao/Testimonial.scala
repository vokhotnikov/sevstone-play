package models.dao

import play.api.db.slick.Profile
import org.joda.time.DateTime

case class TestimonialRecord(id: Option[Long], authorName: String, authorEmail: Option[String], text: String, addedAt: DateTime, isApproved: Boolean)

trait TestimonialComponent { this: Profile =>
  import profile.simple._
  import com.github.tototoshi.slick.JodaSupport._

  class Testimonials extends Table[TestimonialRecord]("testimonials") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def authorName = column[String]("author_name", O.NotNull)
    def authorEmail = column[Option[String]]("author_email")
    def text = column[String]("text", O.NotNull, O.DBType("text"))
    def addedAt = column[DateTime]("added_at", O.NotNull)
    def isApproved = column[Boolean]("is_approved", O.NotNull)

    def * = id.? ~ authorName ~ authorEmail ~ text ~ addedAt ~ isApproved <> (TestimonialRecord, TestimonialRecord.unapply _)
    
    def autoInc = * returning id
  }
}