package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current

trait SpecimenController extends Controller { self: ModelServicesComponent =>

  def show(specimenId: Long) = DBAction { implicit request =>
    Services.SpecimenService.findByIdWithImages(specimenId).map { s =>
      Ok(views.html.specimen.show(s, Services.ArticleService.latestArticles(3)))
    }.getOrElse(NotFound)
  }
}

object SpecimenController extends SpecimenController with ModelServicesComponent