package models

import play.api.db.slick._
import models.dao.SpecimenRecord

case class Specimen(name: String, nameLatin: Option[String], size: Option[String], formula: Option[String], age: Option[String],
  label: String, shortDescription: String, description: String, showOnSite: Boolean, priority: Int,
  category: MaybeLoaded[Category], exposition: MaybeLoaded[Exposition], depositsPlace: MaybeLoaded[DepositsPlace])

trait SpecimensComponent { this: CategoriesComponent with ExpositionsComponent with DepositsPlacesComponent =>
  val DepositsPlaceService: DepositsPlaceService
  
  class SpecimenService extends BasicServiceOps[Specimen] {
    object daoMapping {
      import scala.language.implicitConversions

      implicit def mapToLoaded(v: SpecimenRecord)(implicit session: Session): Loaded[Specimen] =
        Loaded(v.id.get, Specimen(v.name, v.nameLatin, v.size, v.formula, v.age,
            v.label, v.shortDescription, v.description, v.showOnSite, v.priority,
            CategoryService.findById(v.categoryId).get,
            ExpositionService.findById(v.expositionId).get,
            DepositsPlaceService.findById(v.depositsPlaceId).get))

      implicit def mapToRecord(c: Specimen)(implicit session: Session) =
        SpecimenRecord(None, c.name, c.nameLatin, c.size, c.formula, c.age,
            c.label, c.shortDescription, c.description, c.showOnSite, c.priority,
            CategoryService.addIfNeeded(c.category), 
            ExpositionService.addIfNeeded(c.exposition),
            DepositsPlaceService.addIfNeeded(c.depositsPlace))
    }

    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    private val byId = daoService.Specimens.createFinderBy(_.id)

    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)

    def add(specimen: Specimen)(implicit session: Session) = daoService.Specimens.autoInc insert specimen

    def randomSpecimen(implicit session: Session): Option[Loaded[Specimen]] = {
      val q = Query(daoService.Specimens).filter(_.showOnSite)
      val count = q.length.run
      if (count == 0) None 
      else {
        val rnd = (math.random * count).toInt
        Some(q.drop(rnd).take(1).firstOption.map(mapToLoaded).get)
      }
    }
  }
}