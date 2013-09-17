package models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.Config.driver.simple._
import models._
import util.SlickSpecSupport._
import scalaz._
import Scalaz._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DepositsPlaceSpec extends Specification with CrudSpecification[DepositsPlace] {
  def dalObject = DepositsPlaces

  def makeANewValue(suffix: String) = DepositsPlace("deposit-" + suffix, "<p>A test deposit<br>" + suffix + "</p>")

  def asNewValue(a: DepositsPlace) = a.copy(id = None)
  
  "DepositsPlaces DAL object" should {
    "list available places in alphabetical order" in memDB { implicit s: Session =>
      val id1 = DepositsPlaces add DepositsPlace("BBB", "descr1")
      val id2 = DepositsPlaces add DepositsPlace("aaa", "descr2")
      val id3 = DepositsPlaces add DepositsPlace("zzz", "descr2")

      val loaded = DepositsPlaces.findAll.map(_.id).flatten
      loaded must contain(id2, id1, id3).inOrder
    }

    "allow to add long description" in memDB { implicit session: Session =>
      val descr = List.fill(1024)("1234567890").mkString("")

      val id = DepositsPlaces add DepositsPlace("test", descr)

      val loaded = DepositsPlaces.findAll.filter(_.id == id.some).map(_.description).headOption
      loaded must_== Some(descr)
    }

  }
}
