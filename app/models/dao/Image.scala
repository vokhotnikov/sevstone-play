package models.dao

import play.api.db.slick.Profile
import org.joda.time.DateTime

case class ImageRecord(id: Option[Long], url: String, addedAt: DateTime)

trait ImageComponent { this: Profile =>
  import profile.simple._
  import com.github.tototoshi.slick.JodaSupport._

  class Images extends Table[ImageRecord]("images") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def url = column[String]("url", O.NotNull)
    def addedAt = column[DateTime]("added_at", O.NotNull)

    def * = id.? ~ url ~ addedAt <> (ImageRecord, ImageRecord.unapply _)

    def autoInc = * returning id
  }
}