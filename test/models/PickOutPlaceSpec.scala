import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.db.slick.Config.driver.simple._

import models._

import SlickSpecSupport._

class PickOutPlaceSpec extends Specification {
  "PickOutPlaces DAL object" should {
    "return empty list" in memDB { implicit s: Session =>
      PickOutPlaces.findAll must be empty
    }

    "allow to create new place" in memDB { implicit s: Session =>
      val newPlace = NewPickOutPlace("testPlace 3465238745", "<p>Test descr99</p>")
      val id = PickOutPlaces add newPlace

      val loaded = PickOutPlaces.findAll.filter(_.id == id).headOption
      loaded must_== Some(PickOutPlace(id, newPlace.title, newPlace.description))
    }

    "return just added objects" in memDB { implicit s: Session =>
      val id1 = PickOutPlaces add NewPickOutPlace("place1", "descr1")
      val id2 = PickOutPlaces add NewPickOutPlace("place2", "descr2")

      val loaded = PickOutPlaces.findAll.map(_.id)
      loaded must contain(id1, id2)
    }

    "list available places in aplhabetical order" in memDB { implicit s: Session =>
      val id1 = PickOutPlaces add NewPickOutPlace("BBB", "descr1")
      val id2 = PickOutPlaces add NewPickOutPlace("aaa", "descr2")
      val id3 = PickOutPlaces add NewPickOutPlace("zzz", "descr2")

      val loaded = PickOutPlaces.findAll.map(_.id)
      loaded must contain(id2, id1, id3).inOrder
    }
  }
}
