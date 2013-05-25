import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.db.slick.Config.driver.simple._

import models._

import SlickSpecSupport._

class CategorySpec extends Specification with CrudSpecification[Category, NewCategory] {
  def dalObject = Categories

  def makeANewValue(suffix: String) = NewCategory(None, "expo-" + suffix, false, 100)

  "Categories DAL object" should {
    "list available expositions in alphabetical order" in memDB { implicit s: Session =>
      val id1 = Categories add NewCategory(None, "BBB", false, 1)
      val id2 = Categories add NewCategory(None, "aaa", true, 1)
      val id3 = Categories add NewCategory(None, "zzz", false, 1)

      val loaded = Categories.findAll.map(_.id)
      loaded must contain(id2, id1, id3).inOrder
    }

    "support loading of hierarchies" in memDB { implicit session: Session =>
      val id1 = Categories add NewCategory(None, "BBB", false, 3)
      val id2 = Categories add NewCategory(None, "aaa", true, 1)
      val id3 = Categories add NewCategory(Some(id1), "zzz", false, 1)

      val loaded = Categories.loadHierarchies
      val l1 = Categories findById id1
      val l2 = Categories findById id2
      val l3 = Categories findById id3

      loaded must contain(Hierarchy(l2.get, None, List()),
        Hierarchy(l1.get, None, List(Hierarchy(l3.get, l1, List())))).only.inOrder
    }
  }
}
