package models.dao

import play.api.db.slick.Profile

case class CategoryRecord(id: Option[Long], parentId: Option[Long], title: String, isHidden: Boolean, sortPriority: Long)

trait CategoryComponent { this: Profile =>
  import profile.simple._

  class Categories extends Table[CategoryRecord]("categories") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def parentId = column[Option[Long]]("parent_id")
    def title = column[String]("title", O.NotNull)
    def isHidden = column[Boolean]("is_hidden", O.NotNull)
    def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

    def parent = foreignKey("Categories_ParentFK", parentId, this)(_.id)

    def * = id.? ~ parentId ~ title ~ isHidden ~ sortPriority <> (CategoryRecord, CategoryRecord.unapply _)
    
    def autoInc = * returning id
  }
}