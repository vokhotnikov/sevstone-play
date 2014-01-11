package models

import org.specs2.mutable._
import models.dao.current.daoService
import org.specs2.specification.Scope
import daoService.profile.simple._
import play.api.db.slick.DB
import org.joda.time.DateTime
import models.dao.SpecimenPhotoRecord

class SpecimenServiceSpec extends Specification {
  "specimen service" should {
    "return random specimen" in new WithTestDb with TestData {
      DB.withSession { implicit s: Session =>
        service.randomSpecimen should beNone

        val (s1, s2) = makeTestSpecimens

        service add (s1.copy(showOnSite = false))
        service.randomSpecimen should beNone

        val i1 = service add s1
        (1 to 10).map(_ => service.randomSpecimen).map(_.map(_.map(_.specimen))) must_== (1 to 10).map(_ => Some(Loaded(i1, s1)))

        val i2 = service add s2
        (1 to 50).map(_ => service.randomSpecimen).map(_.map(_.map(_.specimen))).toSet must_== Set(Some(Loaded(i1, s1)), Some(Loaded(i2, s2)))
      }
    }

    "load specimen with associated images" in new WithTestDb with TestData {
      DB.withSession { implicit s: Session =>
        val (s1, _) = makeTestSpecimens
        val s1Id = service add s1

        val i1 = Image("/test/a1.jpg", new DateTime())
        val i2 = Image("/test/b2.jpg", new DateTime())

        val i1Id = ImageService add i1
        daoService.SpecimenPhotos.autoInc.insert(SpecimenPhotoRecord(None, s1Id, i1Id, true))

        val i2Id = ImageService add i2
        daoService.SpecimenPhotos.autoInc.insert(SpecimenPhotoRecord(None, s1Id, i2Id, false))

        val l = service.findByIdWithImages(s1Id)

        l should beSome(Loaded(s1Id, SpecimenWithImages(s1, Some(i1), List(i2))))
      }
    }
  }

  trait TestData extends SpecimensComponent
    with CategoriesComponent
    with ExpositionsComponent
    with DepositsPlacesComponent
    with ImagesComponent {
    val SpecimenService = new SpecimenService
    val CategoryService = new CategoryService
    val ExpositionService = new ExpositionService
    val DepositsPlaceService = new DepositsPlaceService
    val ImageService = new ImageService

    val service = SpecimenService

    def makeTestSpecimens(implicit session: Session) = {
      val c1 = CategoryService.findById(CategoryService.add(Category(None, "test parent cat", false, 10))).get
      val c2 = CategoryService.findById(CategoryService.add(Category(Some(c1), "test child cat", false, 2))).get

      val e1 = ExpositionService.findById(ExpositionService.add(Exposition(None, "test parent expo", "cool expo", 12))).get
      val e2 = ExpositionService.findById(ExpositionService.add(Exposition(Some(e1), "test child expo", "not-so-cool expo", 61))).get

      val dp1 = DepositsPlaceService.findById(DepositsPlaceService.add(DepositsPlace("test place one", "DP one"))).get
      val dp2 = DepositsPlaceService.findById(DepositsPlaceService.add(DepositsPlace("test place two", "DP two"))).get

      val s1 = Specimen("sp1", Some("Spl1"), Some("big enough"), Some("H2O"), Some("very old"),
        "lbl1", "short1", "descr1", true, 10, c1, e1, dp1)

      val s2 = Specimen("sp2", None, None, None, None, "lbl2", "short2", "descr2", true, 7, c2, e2, dp2)

      (s1, s2)
    }
  }
}