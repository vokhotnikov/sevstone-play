package controllers.edit

import play.api._
import play.api.mvc.{Controller,Action}
import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import models._

object PickOutPlaces extends Controller {
  def index = Action {
    DB.withSession { implicit session: Session =>
      Ok(views.html.edit.pickoutplaces.index(models.PickOutPlaces.findAll))
    }
  }

  def create = TODO
  def save = TODO

  def edit(id: Long) = TODO
  def update(id:Long) = TODO
}
