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

      val result = controllers.edit.PickOutPlaces.index()(FakeRequest())

      status(result) must_== OK

      contentAsString(result) must contain(np1.title)
      contentAsString(result) must contain(np2.title)
    }
  }
}
