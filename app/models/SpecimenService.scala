package models

import play.api.db.slick._
import models.dao.SpecimenRecord

case class Specimen(name: String, nameLatin: Option[String], size: Option[String], formula: Option[String], age: Option[String],
  label: String, shortDescription: String, description: String, showOnSite: Boolean, priority: Int,
  category: MaybeLoaded[Category], exposition: MaybeLoaded[Exposition], depositsPlace: MaybeLoaded[DepositsPlace])

case class SpecimenWithImages(specimen: Specimen, mainImage: Option[Image], additionaryImages: List[Image])

trait SpecimensComponent { this: CategoriesComponent with ExpositionsComponent with DepositsPlacesComponent with ImagesComponent =>
  val SpecimenService: SpecimenService

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
    def findByIdWithImages(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapWithImages)

    def add(specimen: Specimen)(implicit session: Session) = daoService.Specimens.autoInc insert specimen

    import implicits._
    import ImageService.{ daoMapping => imageDaoMapping }

    private def mapWithImages(r: SpecimenRecord)(implicit session: Session): Loaded[SpecimenWithImages] = {
      mapToLoaded(r).map { s =>
        val p = (for {
          p <- daoService.SpecimenPhotos
          i <- daoService.Images if p.imageId === i.id
        } yield (p, i)).filter(_._1.specimenId === r.id).list
        
        val photos = p.partition(_._1.isMain)

        SpecimenWithImages(s,
          photos._1.headOption.map(i => imageDaoMapping.mapToLoaded(i._2)),
          photos._2.map(p => imageDaoMapping.mapToLoaded(p._2).value))
      }
    }

    def randomSpecimen(implicit session: Session): Option[Loaded[SpecimenWithImages]] = {
      val q = Query(daoService.Specimens).filter(_.showOnSite)
      val count = q.length.run
      if (count == 0) None
      else {
        val rnd = (math.random * count).toInt
        q.drop(rnd).take(1).firstOption.map(mapWithImages)
      }
    }
    
    def search(query: String)(implicit session: Session): List[Loaded[SpecimenWithImages]] = {
      import scala.slick.jdbc.{GetResult, StaticQuery => Q}
      import Q.interpolation
      
      // todo: use normal search backend like elastic
      val likeVal = s"%$query%"
      val foundIds = sql"SELECT id FROM specimens WHERE name ILIKE $likeVal OR name_latin ILIKE $likeVal OR label ILIKE $likeVal OR short_description ILIKE $likeVal".as[Long].list;

      val q = (for {
        s <- daoService.Specimens
        if s.showOnSite
        if s.id inSetBind foundIds
      } yield s).take(100)
      
      val specimens = q.list.map(mapToLoaded)
      val loadedIds = specimens.map(_.id).toSet

      val photos = (for {
        p <- daoService.SpecimenPhotos
        if p.specimenId inSetBind loadedIds
        i <- daoService.Images
        if p.imageId === i.id
      } yield (p, i)).list.groupBy(t => t._1.specimenId)

      specimens.map { s => 
        val specimenPhotos = photos.get(s.id).map(_.partition(_._1.isMain)).getOrElse((List(), List()))
        val mainImage = specimenPhotos._1.headOption.map(t => imageDaoMapping.mapToLoaded(t._2).value)
        val additionalImages = specimenPhotos._2.map(t => imageDaoMapping.mapToLoaded(t._2).value)
        Loaded[SpecimenWithImages](s.id, SpecimenWithImages(s.value, mainImage, additionalImages))
      }
    }
  }
}