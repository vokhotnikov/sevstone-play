package models

import play.api.db.slick.Config.driver.simple._

case class NewExposition(parentId: Option[Long], title: String, description: String, sortPriority: Long)
case class Exposition(id: Long, parentId: Option[Long], title: String, description: String, sortPriority: Long) extends HierarchicalEntity[NewExposition] {
  def asNew = NewExposition(parentId, title, description, sortPriority)
}

object Expositions extends Table[Exposition]("expositions") with CrudSupport[Exposition, NewExposition] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def parentId = column[Option[Long]]("parent_id")
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))
  def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

  def parent = foreignKey("ParentFK", parentId, Expositions)(_.id)

  def * = id ~ parentId ~ title ~ description ~ sortPriority <> (Exposition, Exposition.unapply _)

  def forInsert = parentId ~ title ~ description ~ sortPriority <> (NewExposition, NewExposition.unapply _) returning id

  def findById(id: Long)(implicit session:Session) =
    (for { p <- Expositions if p.id === id } yield p).firstOption

  def findAll(implicit session: Session) = {
    Query(Expositions).sortBy(_.title.toLowerCase).list
  }

  def add(newPlace: NewExposition)(implicit session: Session) = forInsert insert newPlace

  def update(id:Long, newValue: NewExposition)(implicit session: Session) = {
    val q = for { p <- Expositions if p.id === id } yield p.parentId ~ p.title ~ p.description ~ p.sortPriority
    q update ((newValue.parentId, newValue.title, newValue.description, newValue.sortPriority))
  }

  def loadHierarchies(implicit session: Session) = Hierarchy(findAll)
}
