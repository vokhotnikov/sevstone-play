package models

import play.api.db.slick.Config.driver.simple._

case class Exposition(parentId: Option[Long], title: String, description: String, sortPriority: Long, id: Option[Long] = None) extends HierarchicalEntity[Exposition] {
  def withId(newId: Option[Long]) = copy(id = newId)
}

object Expositions extends Table[Exposition]("expositions") with CrudSupport[Exposition] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def parentId = column[Option[Long]]("parent_id")
  def title = column[String]("title", O.NotNull)
  def description = column[String]("description", O.NotNull, O.DBType("text"))
  def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

  def parent = foreignKey("Expositions_ParentFK", parentId, Expositions)(_.id)

  def * = parentId ~ title ~ description ~ sortPriority ~ id.? <> (Exposition, Exposition.unapply _)

  def findAll(implicit session: Session) = {
    Query(Expositions).sortBy(_.title.toLowerCase).list
  }

  def loadHierarchies(implicit session: Session) = Hierarchy(findAll)
}
