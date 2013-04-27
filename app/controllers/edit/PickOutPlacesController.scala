package controllers.edit

import play.api._
import play.api.mvc.{Controller,Action,Request,Result}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import models._

import views.html.edit.pickoutplaces

object PickOutPlacesController extends Controller {
  val pickOutPlaceForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> text
    )(NewPickOutPlace.apply)(NewPickOutPlace.unapply)
  )

  private def withExisting[A](id: Long)(f: PickOutPlace => Result)(implicit request: Request[A]) = {
    DB.withTransaction { implicit session =>
      val place = PickOutPlaces findById id
      place match {
        case None => BadRequest(pickoutplaces.index(PickOutPlaces.findAll)).flashing("error" -> ("Место отбора не найдено: " + id.toString))
        case Some(p) => f(p)
      }
    }
  }

  def index = Action { implicit request =>
    DB.withTransaction { implicit session =>
      Ok(pickoutplaces.index(PickOutPlaces.findAll))
    }
  }

  def create = Action { implicit request =>
    Ok(pickoutplaces.create(pickOutPlaceForm))
  }

  def save = Action { implicit request =>
    pickOutPlaceForm.bindFromRequest.fold(
      errors => {
        BadRequest(pickoutplaces.create(errors))
      },
      newPlace => {
        DB.withTransaction { implicit session =>
          PickOutPlaces add newPlace
        }
        Redirect(routes.PickOutPlacesController.index)
      })
  }

  def edit(id: Long) = Action { implicit request =>
    withExisting(id) { p =>
      Ok(pickoutplaces.edit(p, pickOutPlaceForm.fill(p.asNew)))
    }
  }

  def update(id:Long) = Action { implicit request =>
    pickOutPlaceForm.bindFromRequest.fold(
      errors => {
        withExisting(id) { p =>
          BadRequest(pickoutplaces.edit(p, errors))
        }
      },
      newPlaceValue => {
        DB.withTransaction { implicit session =>
          PickOutPlaces.update(id, newPlaceValue)
        }
        Redirect(routes.PickOutPlacesController.index)
      })
  }
}
