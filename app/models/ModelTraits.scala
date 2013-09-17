package models

import play.api.db.slick.Config.driver.simple._

import scalaz._
import Scalaz._

trait ModelEntity[E <: ModelEntity[E]] { self: E =>
  def id: Option[Long]
  def withId(newId: Option[Long]): E
}

trait CrudSupport[A <: ModelEntity[A]] { this: Table[A] =>
  def id: Column[Long]
  def * : scala.slick.lifted.ColumnBase[A]
  
  def autoInc = * returning id 
  
  def findById(id: Long)(implicit session:Session) =
    (for { p <- tableToQuery(this) if p.id === id } yield p).firstOption

  def add(newValue: A)(implicit session: Session): Long = autoInc insert newValue

  def update(id: Long, newValue: A)(implicit session: Session): Long = {
    (tableToQuery(this).where(_.id === id)).update(newValue.withId(id.some))
  }
  
  def findAll(implicit session: Session): List[A]
}

