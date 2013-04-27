package models

import play.api.db.slick.Config.driver.simple._

case class NewPickOutPlace(title: String, description: String)
case class PickOutPlace(id: Long, title: String, description: String) {
  def asNew = NewPickOutPlace(title, description)
}

object PickOutPlaces extends Table[PickOutPlace]("pick_out_places") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))

  def * = id ~ title ~ description <> (PickOutPlace, PickOutPlace.unapply _)

  def forInsert = title ~ description <> (NewPickOutPlace, NewPickOutPlace.unapply _) returning id

  def findById(id: Long)(implicit session:Session) =
    (for { p <- PickOutPlaces if p.id === id } yield p).firstOption

  def findAll(implicit session: Session) = {
    Query(PickOutPlaces).sortBy(_.title.toLowerCase).list
  }

  def add(newPlace: NewPickOutPlace)(implicit session: Session) = forInsert insert newPlace

  def update(id:Long, newValue: NewPickOutPlace)(implicit session: Session) = {
    val q = for { p <- PickOutPlaces if p.id === id } yield p.title ~ p.description
    q update (newValue.title, newValue.description)
  }
}
