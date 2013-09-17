package models

import play.api.db.slick.Config.driver.simple._

case class PickOutPlace(title: String, description: String, id: Option[Long] = None) extends ModelEntity[PickOutPlace] {
  def withId(newId: Option[Long]) = copy(id = newId)
}

object PickOutPlaces extends Table[PickOutPlace]("pick_out_places")  with CrudSupport[PickOutPlace]{
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))

  def * = title ~ description ~ id.? <> (PickOutPlace, PickOutPlace.unapply _)

  def findAll(implicit session: Session) = {
    Query(PickOutPlaces).sortBy(_.title.toLowerCase).list
  }
}
