package models

import play.api.db.slick.Config.driver.simple.Session

trait CrudSupport[A, NA] {
  def findAll(implicit session: Session): List[A]
  def findById(id: Long)(implicit session: Session): Option[A]
  def add(newValue: NA)(implicit session: Session): Long
  def update(id: Long, newValue: NA)(implicit session: Session): Long
}

trait ModelEntity[NA] {
  def id: Long
  def asNew:NA
}

