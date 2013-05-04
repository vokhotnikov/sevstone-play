package models

import play.api.db.slick.Config.driver.simple._

case class NewDepositsPlace(title: String, description: String)
case class DepositsPlace(id: Long, title: String, description: String) extends ModelEntity[NewDepositsPlace] {
  def asNew = NewDepositsPlace(title, description)
}

object DepositsPlaces extends Table[DepositsPlace]("deposits_places") with CrudSupport[DepositsPlace, NewDepositsPlace] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))

  def * = id ~ title ~ description <> (DepositsPlace, DepositsPlace.unapply _)

  def forInsert = title ~ description <> (NewDepositsPlace, NewDepositsPlace.unapply _) returning id

  def findById(id: Long)(implicit session:Session) =
    (for { p <- DepositsPlaces if p.id === id } yield p).firstOption

  def findAll(implicit session: Session) = {
    Query(DepositsPlaces).sortBy(_.title.toLowerCase).list
  }

  def add(newPlace: NewDepositsPlace)(implicit session: Session) = forInsert insert newPlace

  def update(id:Long, newValue: NewDepositsPlace)(implicit session: Session) = {
    val q = for { p <- DepositsPlaces if p.id === id } yield p.title ~ p.description
    q update (newValue.title, newValue.description)
  }
}
