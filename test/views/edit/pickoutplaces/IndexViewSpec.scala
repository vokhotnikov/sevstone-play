package views.edit.pickoutplaces

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.mvc.Flash

import models._

import scalaz._
import Scalaz._

class PickOutPlacesListViewSpec extends Specification {
  "pick-out places list view" should {
    "render titles for all passed in places" in {
      val places = List(PickOutPlace("First test place", "<p>111</p>", 132l.some), PickOutPlace("Second test place", "222", 15l.some))
      val html = views.html.edit.pickoutplaces.index(places)(new Flash())
      contentAsString(html) must contain("First test place")
      contentAsString(html) must contain("Second test place")
    }

    "render message about absent data when no places is passed" in {
      val html = views.html.edit.pickoutplaces.index(List())(new Flash())
      contentAsString(html) must contain("Пока не добавлено ни одного места отбора.")
    }
  }
}
