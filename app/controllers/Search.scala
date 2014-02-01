package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current

trait SearchController extends Controller { this: ModelServicesComponent =>
  def search = DBAction { implicit request =>
    val query = request.queryString.get("query").flatMap(_.headOption)
    query.map { q =>
      val specimens = Services.SpecimenService.search(Some(q), None, None, None).groupBy(_.value.specimen.category)
      Ok(views.html.search.results(q, specimens))
    }.getOrElse(NotFound)
  }
}

object SearchController extends SearchController with ModelServicesComponent