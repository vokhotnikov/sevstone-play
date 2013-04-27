import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import play.api.db.slick.Config.driver.simple._

import models._

import SlickSpecSupport._

class PickOutPlacesControllerSpec extends Specification {
  "pick out places controller index method" should {
    "display all available places" in memDB { implicit session: Session =>
      val np1 = NewPickOutPlace("Some imaginary place", "A description")
      val np2 = NewPickOutPlace("Another place", "Really?!")

      PickOutPlaces add np1
      PickOutPlaces add np2

      val result = controllers.edit.PickOutPlacesController.index()(FakeRequest())

      status(result) must_== OK

      contentAsString(result) must contain(np1.title)
      contentAsString(result) must contain(np2.title)
    }
  }

  "save new pick out place method" should {
    "create new place in database" in memDB { implicit session: Session =>
      val title = "New place 234521"
      val descr = "<p>A descr 9931450</p>"

      val result = controllers.edit.PickOutPlacesController.save()(
        FakeRequest().withFormUrlEncodedBody("title" -> title, "description" -> descr))

      val loaded = PickOutPlaces.findAll.headOption.map(p => (p.title, p.description))

      loaded must_== Some(title, descr)
    }
  }

  "update existing pick out place method" should {
    "create new place in database" in memDB { implicit session: Session =>
      val title = "New place 234521"
      val descr = "<p>A descr 9931450</p>"
      val id = PickOutPlaces add NewPickOutPlace(title, descr)

      val result = controllers.edit.PickOutPlacesController.update(id)(
        FakeRequest().withFormUrlEncodedBody("title" -> (title + "-updated"), "description" -> (descr + "-updated")))

      val loaded = PickOutPlaces.findAll.headOption.map(p => (p.id, p.title, p.description))

      loaded must_== Some(id, title + "-updated", descr + "-updated")
    }
  }
}
