package controllers

import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import models._

case class CollectionFilters(keyword: Option[String], categoryId: Option[Long],
  depositsPlaceId: Option[Long], expositionId: Option[Long])
case class CollectionFilterAvailableSets(categories: List[Hierarchy[Loaded[Category]]],
  depositsPlaces: List[Loaded[DepositsPlace]],
  expositions: List[Hierarchy[Loaded[Exposition]]])

trait CollectionController extends Controller { this: ModelServicesComponent =>

  val filterForm = play.api.data.Form(
    mapping(
      "keyword" -> optional(text),
      "category" -> optional(longNumber),
      "depositsPlace" -> optional(longNumber),
      "exposition" -> optional(longNumber))(CollectionFilters.apply)(CollectionFilters.unapply))

  def expositions = DBAction { implicit request =>
    Ok(views.html.collection.expositions(Services.ExpositionService.loadAllTrees))
  }

  def query = DBAction { implicit request =>
    import util.StringUtil._

    filterForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      filters => {
          val available = CollectionFilterAvailableSets(Services.CategoryService.loadAllTrees,
            Services.DepositsPlaceService.loadAll,
            Services.ExpositionService.loadAllTrees)

        if (List(filters.keyword, filters.categoryId, filters.depositsPlaceId, filters.expositionId).flatten.isEmpty) {
          Ok(views.html.collection.collection(filterForm, available))
        } else {
          val categories = filters.categoryId.flatMap(Services.CategoryService.findSubtree(_))
          val depositsPlace = filters.depositsPlaceId.flatMap(Services.DepositsPlaceService.findById(_))
          val expos = filters.expositionId.flatMap(Services.ExpositionService.findSubtree(_))

          val title = List(
            categories.map(_.node.value.title),
            depositsPlace.map(_.value.title),
            expos.map(_.node.value.title),
            filters.keyword.map(q => s"Результаты поиска для $q")).flatten.headOption.getOrElse("Коллекция")

          val results = Services.SpecimenService.search(filters.keyword, categories, depositsPlace, expos)

          Ok(views.html.collection.results(results, title, filterForm.bindFromRequest(), available))
        }
      })
  }
}

object CollectionController extends CollectionController with ModelServicesComponent