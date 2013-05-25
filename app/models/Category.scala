package models

import play.api.db.slick.Config.driver.simple._

case class NewCategory(parentId: Option[Long], title: String, isHidden: Boolean, sortPriority: Long)
case class Category(id: Long, parentId: Option[Long], title: String, isHidden: Boolean, sortPriority: Long) extends HierarchicalEntity[NewCategory] {
  def asNew = NewCategory(parentId, title, isHidden, sortPriority)
}

object Categories extends Table[Category]("categories") with CrudSupport[Category, NewCategory] {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def parentId = column[Option[Long]]("parent_id")
  def title = column[String]("title", O.NotNull)
  def isHidden = column[Boolean]("is_hidden", O.NotNull)
  def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

  def parent = foreignKey("Categories_ParentFK", parentId, Categories)(_.id)

  def * = id ~ parentId ~ title ~ isHidden ~ sortPriority <> (Category, Category.unapply _)

  def forInsert = parentId ~ title ~ isHidden ~ sortPriority <> (NewCategory, NewCategory.unapply _) returning id

  def findById(id: Long)(implicit session:Session) =
    (for { p <- Categories if p.id === id } yield p).firstOption

  def findAll(implicit session: Session) = {
    Query(Categories).sortBy(_.title.toLowerCase).list
  }

  def add(newCategory: NewCategory)(implicit session: Session) = forInsert insert newCategory

  def update(id:Long, newValue: NewCategory)(implicit session: Session) = {
    val q = for { p <- Categories if p.id === id } yield p.parentId ~ p.title ~ p.isHidden ~ p.sortPriority
    q update ((newValue.parentId, newValue.title, newValue.isHidden, newValue.sortPriority))
  }

  def loadHierarchies(implicit session: Session) = Hierarchy(findAll)
}
