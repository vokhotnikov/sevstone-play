import org.specs2.mutable.Specification

import play.api.db.slick.Config.driver.simple.Session

import models._

import SlickSpecSupport._

import scalaz._
import Scalaz._

trait CrudSpecification[A <: ModelEntity] { self: Specification =>
  def dalObject: CrudSupport[A]

  def makeANewValue(suffix: String): A
  
  def asNewValue(a: A): A

  def toIdAndNewProjection(a: A) = (a.id, asNewValue(a))
  
  ("CRUD support for " + self.getClass().getName()) should {

    "return empty list" in memDB { implicit s: Session =>
      dalObject.findAll must be empty
    }

    "allow to create new record" in memDB { implicit s: Session =>
      val newPlace = makeANewValue("243672")
      val id = dalObject add newPlace

      val loaded = dalObject.findAll.filter(_.id == id.some).map(asNewValue).headOption
      loaded must_== Some(newPlace)
    }

    "return just added objects" in memDB { implicit s: Session =>
      val id1 = dalObject add makeANewValue("1")
      val id2 = dalObject add makeANewValue("2")

      val loaded = dalObject.findAll.map(_.id).flatten
      loaded must contain(id1, id2)
    }

    "search existing object by id" in memDB { implicit session: Session =>
      val new1 = makeANewValue("1")
      val new2 = makeANewValue("2")

      val id1 = dalObject add new1
      val id2 = dalObject add new2

      dalObject.findById(id1).map(toIdAndNewProjection) must_== Some(id1.some, new1)
      dalObject.findById(id2).map(toIdAndNewProjection) must_== Some(id2.some, new2)
      dalObject.findById(id2 + 1000000) must_== None
    }

    "allow to update existing record" in memDB { implicit session: Session =>
      val new1 = makeANewValue("1")
      val new2 = makeANewValue("2")

      val id1 = dalObject add new1
      val id2 = dalObject add new2

      val newUpdated = makeANewValue("updated")
      val count = dalObject.update(id2, newUpdated)

      val loaded = dalObject.findById(id2).map(asNewValue)
      val unchanged = dalObject.findById(id1).map(asNewValue)
      unchanged must_== Some(new1)
    }

    "return zero count when updating non-existing record" in memDB { implicit session: Session =>
      val count = dalObject.update(10000000, makeANewValue("non-existing"))
      count must_== 0
      dalObject.findAll.length must_== 0
    }
  }
}
