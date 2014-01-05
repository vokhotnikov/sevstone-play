package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current

trait ArticlesController extends Controller { this: ModelServicesComponent =>
  def list() = DBAction { implicit request =>
    Ok(views.html.articles.list(Services.ArticleService.allArticles))
  }
  
  def show(articleId: Long) = DBAction { implicit request =>
    Ok("todo")
  }
}

object ArticlesController extends ArticlesController with ModelServicesComponent