package models

import play.api.db.slick.Config.driver.simple._

case class PickOutPlace(title: String, description: String, id: Option[Long] = None) extends ModelEntity

object PickOutPlaces extends Table[PickOutPlace]("pick_out_places")  with CrudSupport[PickOutPlace]{
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))

  def * = title ~ description ~ id.? <> (PickOutPlace, PickOutPlace.unapply _)

  def autoInc = * returning id
  
  def findById(id: Long)(implicit session:Session) =
    (for { p <- PickOutPlaces if p.id === id } yield p).firstOption

  def findAll(implicit session: Session) = {
    Query(PickOutPlaces).sortBy(_.title.toLowerCase).list
  }

  def add(newPlace: PickOutPlace)(implicit session: Session) = autoInc insert newPlace

  def update(id:Long, newValue: PickOutPlace)(implicit session: Session) = {
    val q = for { p <- PickOutPlaces if p.id === id } yield p.title ~ p.description
    q update (newValue.title, newValue.description)
  }
}
