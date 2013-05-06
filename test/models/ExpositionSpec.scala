import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.db.slick.Config.driver.simple._

import models._

import SlickSpecSupport._

class ExpositionSpec extends Specification with CrudSpecification[Exposition, NewExposition] {
  def dalObject = Expositions

  def makeANewValue(suffix: String) = NewExposition(None, "expo-" + suffix, "<p>A test exposition<br>" + suffix + "</p>", 100)

  "Expositions DAL object" should {
    "list available expositions in alphabetical order" in memDB { implicit s: Session =>
      val id1 = Expositions add NewExposition(None, "BBB", "descr1", 1)
      val id2 = Expositions add NewExposition(None, "aaa", "descr2", 1)
      val id3 = Expositions add NewExposition(None, "zzz", "descr2", 1)

      val loaded = Expositions.findAll.map(_.id)
      loaded must contain(id2, id1, id3).inOrder
    }

    "allow to add long description" in memDB { implicit session: Session =>
      val descr = List.fill(1024)("1234567890").mkString("")

      val id = Expositions add NewExposition(None, "test", descr, 9)

      val loaded = Expositions.findAll.filter(_.id == id).map(_.description).headOption
      loaded must_== Some(descr)
    }

  }
}
