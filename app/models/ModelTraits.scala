package models

import play.api.db.slick._

abstract sealed trait MaybeLoaded[+A] {
  def value: A
}

case class NotLoaded[+A](val value: A) extends MaybeLoaded[A]
case class Loaded[+A](val id: Long, val value: A) extends MaybeLoaded[A] {
  def map[U](f: A => U): Loaded[U] = Loaded(id, f(value))
}

trait BasicServiceOps[A] {
  def findById(id: Long)(implicit session: Session): Option[Loaded[A]]
  
  def add(v: A)(implicit session: Session): Long
  
  def addIfNeeded(a: MaybeLoaded[A])(implicit session: Session) = a match {
    case Loaded(id, v) => id
    case NotLoaded(v) => add(v) 
  }
}

object implicits {
  import scala.language.implicitConversions
  
  implicit def unwrapMaybeLoaded[A](loaded: MaybeLoaded[A]) = loaded.value
}