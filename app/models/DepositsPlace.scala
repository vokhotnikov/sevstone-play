package models

import play.api.db.slick.Config.driver.simple._

case class DepositsPlace(title: String, description: String, id: Option[Long] = None) extends ModelEntity

object DepositsPlaces extends Table[DepositsPlace]("deposits_places") with CrudSupport[DepositsPlace] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))

  def * = title ~ description ~ id.? <> (DepositsPlace, DepositsPlace.unapply _)

  def autoInc = * returning id
  
  def findById(id: Long)(implicit session:Session) =
    (for { p <- DepositsPlaces if p.id === id } yield p).firstOption

  def findAll(implicit session: Session) = {
    Query(DepositsPlaces).sortBy(_.title.toLowerCase).list
  }

  def add(newPlace: DepositsPlace)(implicit session: Session) = autoInc insert newPlace

  def update(id:Long, newValue: DepositsPlace)(implicit session: Session) = {
    val q = for { p <- DepositsPlaces if p.id === id } yield p.title ~ p.description
    q update (newValue.title, newValue.description)
  }
}
