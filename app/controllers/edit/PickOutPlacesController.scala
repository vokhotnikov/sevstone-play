package controllers.edit

import play.api._
import play.api.mvc.{Controller,Action}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import models._

object PickOutPlacesController extends Controller {
  val pickOutPlaceForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> text
    )(NewPickOutPlace.apply)(NewPickOutPlace.unapply)
  )

  def index = Action {
    DB.withSession { implicit session: Session =>
      Ok(views.html.edit.pickoutplaces.index(PickOutPlaces.findAll))
    }
  }

  def create = Action {
    Ok(views.html.edit.pickoutplaces.create(pickOutPlaceForm))
  }

  def save = Action { implicit request =>
    pickOutPlaceForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.edit.pickoutplaces.create(errors))
      },
      newPlace => {
        DB.withSession { implicit session: Session =>
          PickOutPlaces add newPlace
        }
        Redirect(routes.PickOutPlacesController.index)
      })
  }

  def edit(id: Long) = Action {
    DB.withSession { implicit session: Session =>
      val place = PickOutPlaces.findById(id)
      place match {
        case None => BadRequest(views.html.edit.pickoutplaces.index(PickOutPlaces.findAll))
        case Some(p) => Ok(views.html.edit.pickoutplaces.edit(p, pickOutPlaceForm.fill(p.asNew)))
      }
    }
  }

  def update(id:Long) = Action { implicit request =>
    DB.withSession { implicit session: Session =>
      pickOutPlaceForm.bindFromRequest.fold(
        errors => {
          val place = PickOutPlaces.findById(id)
          place match {
            case None => BadRequest(views.html.edit.pickoutplaces.index(PickOutPlaces.findAll))
            case Some(p) => BadRequest(views.html.edit.pickoutplaces.edit(p, errors))
          }
        },
        newPlaceValue => {
          PickOutPlaces.update(id, newPlaceValue)
          Redirect(routes.PickOutPlacesController.index)
        })
    }
  }
}
