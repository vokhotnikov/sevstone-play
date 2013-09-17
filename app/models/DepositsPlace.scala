package models

import play.api.db.slick.Config.driver.simple._

case class DepositsPlace(title: String, description: String, id: Option[Long] = None) extends ModelEntity[DepositsPlace] {
  def withId(newId: Option[Long]) = copy(id = newId)
}

object DepositsPlaces extends Table[DepositsPlace]("deposits_places") with CrudSupport[DepositsPlace] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))

  def * = title ~ description ~ id.? <> (DepositsPlace, DepositsPlace.unapply _)

  def findAll(implicit session: Session) = {
    Query(DepositsPlaces).sortBy(_.title.toLowerCase).list
  }
}
