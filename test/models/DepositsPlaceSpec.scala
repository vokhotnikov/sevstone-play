import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.db.slick.Config.driver.simple._

import models._

import SlickSpecSupport._

class DepositsPlaceSpec extends Specification with CrudSpecification[DepositsPlace, NewDepositsPlace] {
  def dalObject = DepositsPlaces

  def makeANewValue(suffix: String) = NewDepositsPlace("deposit-" + suffix, "<p>A test deposit<br>" + suffix + "</p>")

  "DepositsPlaces DAL object" should {
    "list available places in alphabetical order" in memDB { implicit s: Session =>
      val id1 = DepositsPlaces add NewDepositsPlace("BBB", "descr1")
      val id2 = DepositsPlaces add NewDepositsPlace("aaa", "descr2")
      val id3 = DepositsPlaces add NewDepositsPlace("zzz", "descr2")

      val loaded = DepositsPlaces.findAll.map(_.id)
      loaded must contain(id2, id1, id3).inOrder
    }

    "allow to add long description" in memDB { implicit session: Session =>
      val descr = List.fill(1024)("1234567890").mkString("")

      val id = DepositsPlaces add NewDepositsPlace("test", descr)

      val loaded = DepositsPlaces.findAll.filter(_.id == id).map(_.description).headOption
      loaded must_== Some(descr)
    }

  }
}
