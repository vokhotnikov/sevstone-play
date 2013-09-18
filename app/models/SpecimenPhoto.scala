package models

import play.api.db.slick.Config.driver.simple._

case class SpecimenPhoto(specimenId: Long, imageId: Long, isMain: Boolean, id: Option[Long] = None) extends ModelEntity[SpecimenPhoto] {
  def withId(newId: Option[Long]) = copy(id = newId)
}

object SpecimenPhotos extends Table[SpecimenPhoto]("specimen_photos") with CrudSupport[SpecimenPhoto] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def specimenId = column[Long]("specimen_id", O.NotNull)
  def imageId = column[Long]("image_id", O.NotNull)
  def isMain = column[Boolean]("is_main", O.NotNull)
  
  def specimen = foreignKey("SpecimenPhotos_SpecimenFK", specimenId, Specimens)(_.id)
  def image = foreignKey("SpecimenPhotos_ImageFK", imageId, Images)(_.id)

  def * = specimenId ~ imageId ~ isMain ~ id.? <> (SpecimenPhoto, SpecimenPhoto.unapply _)
  
  def findAll(implicit session: Session) = Query(SpecimenPhotos).sortBy(_.specimenId).list 
}