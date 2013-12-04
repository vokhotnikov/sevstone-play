package models.dao

import play.api.db.slick.Profile

case class ExpositionRecord(id: Option[Long], parentId: Option[Long], title: String, description: String, sortPriority: Long)

trait ExpositionComponent { this: Profile =>
  import profile.simple._

  class Expositions extends Table[ExpositionRecord]("expositions") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def parentId = column[Option[Long]]("parent_id")
    def title = column[String]("title", O.NotNull)
    def description = column[String]("description", O.NotNull, O.DBType("text"))
    def sortPriority = column[Long]("sort_priority", O.NotNull, O.Default(0))

    def parent = foreignKey("Expositions_ParentFK", parentId, this)(_.id)

    def * = id.? ~ parentId ~ title ~ description ~ sortPriority <> (ExpositionRecord, ExpositionRecord.unapply _)
    
    def autoInc = * returning id
  }
}