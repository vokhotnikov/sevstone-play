package models

import play.api.db.slick.Config.driver.simple.Session

trait CrudSupport[A] {
  def findAll(implicit session: Session): List[A]
  def findById(id: Long)(implicit session: Session): Option[A]
  def add(newValue: A)(implicit session: Session): Long
  def update(id: Long, newValue: A)(implicit session: Session): Long
}

trait ModelEntity {
  def id: Option[Long]
}

