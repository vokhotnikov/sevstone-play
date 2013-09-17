package models

import play.api.db.slick.Config.driver.simple._

case class Category(parentId: Option[Long], title: String, isHidden: Boolean, sortPriority: Long, id: Option[Long] = None) extends HierarchicalEntity[Category] {
  def withId(newId: Option[Long]) = copy(id = newId)
}

object Categories extends Table[Category]("categories") with CrudSupport[Category] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def parentId = column[Option[Long]]("parent_id")
  def title = column[String]("title", O.NotNull)
  def isHidden = column[Boolean]("is_hidden", O.NotNull)
  def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

  def parent = foreignKey("Categories_ParentFK", parentId, Categories)(_.id)

  def * = parentId ~ title ~ isHidden ~ sortPriority ~ id.? <> (Category, Category.unapply _)

  def findAll(implicit session: Session) = {
    Query(Categories).sortBy(_.title.toLowerCase).list
  }

  def loadHierarchies(implicit session: Session) = Hierarchy(findAll)
}
