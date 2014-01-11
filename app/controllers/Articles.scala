package controllers

import models._
import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.Play.current

trait ArticlesController extends Controller { this: ModelServicesComponent =>
  def list() = DBAction { implicit request =>
    Ok(views.html.article.list(Services.ArticleService.allArticles))
  }
  
  def show(articleId: Long) = DBAction { implicit request =>
    Services.ArticleService.findById(articleId).map {a => 
      Ok(views.html.article.show(a))
    }.getOrElse(NotFound)
  }
}

object ArticlesController extends ArticlesController with ModelServicesComponent