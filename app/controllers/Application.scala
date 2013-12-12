package controllers

import play.api._
import play.api.mvc._
import play.api.db.slick._
import models._
import play.api.Play.current

case class HomeViewModel(randomSpecimen: Option[Loaded[SpecimenWithImages]],
  latestArticles: List[Loaded[Article]], latestTestimonials: List[Loaded[Testimonial]])

trait ApplicationController extends Controller { this: ModelServicesComponent =>
  def home = DBAction { implicit rs =>
    val model = HomeViewModel(
      Services.SpecimenService.randomSpecimen,
      Services.ArticleService.latestArticles(3),
      Services.TestimonialService.latestTestimonials(3))

    Ok(views.html.app.home(model))
  }
}

object ApplicationController extends ApplicationController with ModelServicesComponent