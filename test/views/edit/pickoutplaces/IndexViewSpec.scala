import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import models._

class PickOutPlacesListViewSpec extends Specification {
  "pick-out places list view" should {
    "render titles for all passed in places" in {
      val places = List(PickOutPlace(132, "First test place", "<p>111</p>"), PickOutPlace(15, "Second test place", "222"))
      val html = views.html.edit.pickoutplaces.index(places)
      contentAsString(html) must contain("First test place")
      contentAsString(html) must contain("Second test place")
    }

    "render message about absent data when no places is passed" in {
      val html = views.html.edit.pickoutplaces.index(List())
      contentAsString(html) must contain("Пока не добавлено ни одного места отбора.")
    }
  }
}
