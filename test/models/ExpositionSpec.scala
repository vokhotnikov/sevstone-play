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
class ExpositionSpec extends Specification with CrudSpecification[Exposition] {
  def dalObject = Expositions

  def makeANewValue(suffix: String) = Exposition(None, "expo-" + suffix, "<p>A test exposition<br>" + suffix + "</p>", 100)

  def asNewValue(a: Exposition) = a.copy(id = None)
  
  "Expositions DAL object" should {
    "list available expositions in alphabetical order" in memDB { implicit s: Session =>
      val id1 = Expositions add Exposition(None, "BBB", "descr1", 1)
      val id2 = Expositions add Exposition(None, "aaa", "descr2", 1)
      val id3 = Expositions add Exposition(None, "zzz", "descr2", 1)

      val loaded = Expositions.findAll.map(_.id).flatten
      loaded must contain(id2, id1, id3).inOrder
    }

    "allow to add long description" in memDB { implicit session: Session =>
      val descr = List.fill(1024)("1234567890").mkString("")

      val id = Expositions add Exposition(None, "test", descr, 9)

      val loaded = Expositions.findAll.filter(_.id == id.some).map(_.description).headOption
      loaded must_== Some(descr)
    }


    "support loading of hierarchies" in memDB { implicit session: Session =>
      val id1 = Expositions add Exposition(None, "BBB", "descr1", 3)
      val id2 = Expositions add Exposition(None, "aaa", "descr2", 1)
      val id3 = Expositions add Exposition(Some(id1), "zzz", "descr2", 1)

      val loaded = Expositions.loadHierarchies
      val l1 = Expositions findById id1
      val l2 = Expositions findById id2
      val l3 = Expositions findById id3

      loaded must contain(Hierarchy(l2.get, None, List()),
        Hierarchy(l1.get, None, List(Hierarchy(l3.get, l1, List())))).exactly.inOrder
    }
  }
}
