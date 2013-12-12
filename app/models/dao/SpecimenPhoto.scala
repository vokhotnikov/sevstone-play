package models.dao

import play.api.db.slick.Profile

case class SpecimenPhotoRecord(id: Option[Long], specimenId: Long, imageId: Long, isMain: Boolean)

trait SpecimenPhotoComponent { this: Profile with SpecimenComponent with ImageComponent =>
  val Specimens: Specimens
  val Images: Images

  import profile.simple._

  class SpecimenPhotos extends Table[SpecimenPhotoRecord]("specimen_photos") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def specimenId = column[Long]("specimen_id", O.NotNull)
    def imageId = column[Long]("image_id", O.NotNull)
    def isMain = column[Boolean]("is_main", O.NotNull)

    def specimen = foreignKey("SpecimenPhotos_SpecimenFK", specimenId, Specimens)(_.id)
    def image = foreignKey("SpecimenPhotos_ImageFK", imageId, Images)(_.id)
    
    def * = id.? ~ specimenId ~ imageId ~ isMain <> (SpecimenPhotoRecord, SpecimenPhotoRecord.unapply _)
    
    def autoInc = * returning id
  }
}