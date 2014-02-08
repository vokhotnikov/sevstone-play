package models.dao

import play.api.db.slick.Profile
import org.joda.time.DateTime

case class ImageRecord(id: Option[Long], url: String, addedAt: DateTime, fileUID: Option[String])

trait ImageComponent { this: Profile =>
  import profile.simple._
  import com.github.tototoshi.slick.JodaSupport._

  class Images extends Table[ImageRecord]("images") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def url = column[String]("url", O.NotNull)
    def addedAt = column[DateTime]("added_at", O.NotNull)
    def fileUID = column[Option[String]]("file_uid")

    def * = id.? ~ url ~ addedAt ~ fileUID <> (ImageRecord, ImageRecord.unapply _)

    def autoInc = (url ~ addedAt ~ fileUID) returning id
  }
}