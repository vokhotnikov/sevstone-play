import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.db.slick.Config.driver.simple._

import models._

import SlickSpecSupport._

class PickOutPlaceSpec extends Specification with CrudSpecification[PickOutPlace, NewPickOutPlace] {
  def dalObject = PickOutPlaces

  def makeANewValue(suffix: String) = NewPickOutPlace("pick-out-" + suffix, "<p>This is a test place <b>" + suffix + "</b></p>")

  "PickOutPlaces DAL object" should {
    "list available places in alphabetical order" in memDB { implicit s: Session =>
      val id1 = PickOutPlaces add NewPickOutPlace("BBB", "descr1")
      val id2 = PickOutPlaces add NewPickOutPlace("aaa", "descr2")
      val id3 = PickOutPlaces add NewPickOutPlace("zzz", "descr2")

      val loaded = PickOutPlaces.findAll.map(_.id)
      loaded must contain(id2, id1, id3).inOrder
    }

    "allow to add long description" in memDB { implicit session: Session =>
      val descr = List.fill(1024)("1234567890").mkString("")

      val id = PickOutPlaces add NewPickOutPlace("test", descr)

      val loaded = PickOutPlaces.findAll.filter(_.id == id).map(_.description).headOption
      loaded must_== Some(descr)
    }
  }
}
