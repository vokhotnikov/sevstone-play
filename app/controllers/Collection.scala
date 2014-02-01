package controllers

import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import models._

case class CollectionFilters(keyword: Option[String], expositionId: Option[Long])
case class CollectionFilterAvailableSets(expositions: List[Hierarchy[Loaded[Exposition]]])

trait CollectionController extends Controller { this: ModelServicesComponent =>

  val filterForm = play.api.data.Form(
    mapping(
      "keyword" -> optional(text),
      "exposition" -> optional(longNumber))(CollectionFilters.apply)(CollectionFilters.unapply))

  def expositions = DBAction { implicit request =>
    Ok(views.html.collection.expositions(Services.ExpositionService.loadAllTrees))
  }

  def query = DBAction { implicit request =>
    import util.StringUtil._

    filterForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      filters => {
        val expos = filters.expositionId.flatMap(Services.ExpositionService.findSubtree(_))
        val results = Services.SpecimenService.search(filters.keyword, expos)
        val available = CollectionFilterAvailableSets(Services.ExpositionService.loadAllTrees)
        val title = expos.map(_.node.value.title).getOrElse("Коллекция")
        Ok(views.html.collection.results(results, title, filterForm.bindFromRequest(), available))
      })
  }
}

object CollectionController extends CollectionController with ModelServicesComponent