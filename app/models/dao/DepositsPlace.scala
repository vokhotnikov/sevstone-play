package models.dao

import play.api.db.slick.Profile

case class DepositsPlaceRecord(id: Option[Long], title: String, description: String)

trait DepositsPlaceComponent { this: Profile =>
  import profile.simple._

  class DepositsPlaces extends Table[DepositsPlaceRecord]("deposits_places") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title", O.NotNull)
    def description = column[String]("description", O.NotNull, O.DBType("text"))

    def * = id.? ~ title ~ description <> (DepositsPlaceRecord, DepositsPlaceRecord.unapply _)
    
    def autoInc = * returning id
  }
}