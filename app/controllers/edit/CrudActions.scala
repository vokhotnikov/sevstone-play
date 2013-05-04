package controllers.edit

import play.api._
import play.api.mvc.{Controller,Action,Request,Result,Flash,Call}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.templates.Html

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import models.ModelEntity
import models.CrudSupport

trait CrudActions[A <: ModelEntity[NA] ,NA] { self: Controller =>
  def dalObject: CrudSupport[A, NA]

  def crudEditForm: Form[NA]

  def indexRoute: Call

  def indexView[B](implicit request: Request[B], all: List[A]): Html
  def createView[B](implicit request: Request[B], form: Form[NA]): Html
  def editView[B](implicit request: Request[B], a: A, form: Form[NA]): Html

  def notFoundErrorText(details: String): String

  private def withExisting[B](id: Long)(f: A => Result)(implicit request: Request[B]) = {
    DB.withTransaction { implicit session =>
      val place = dalObject findById id
      place match {
        case None => BadRequest(indexView(request, dalObject.findAll)).flashing("error" -> notFoundErrorText(id.toString))
        case Some(p) => f(p)
      }
    }
  }

  def index = Action { implicit request =>
    DB.withTransaction { implicit session =>
      Ok(indexView(request, dalObject.findAll))
    }
  }

  def create = Action { implicit request =>
    Ok(createView(request, crudEditForm))
  }

  def save = Action { implicit request =>
    crudEditForm.bindFromRequest.fold(
      errors => {
        BadRequest(createView(request, errors))
      },
      newValue => {
        DB.withTransaction { implicit session =>
          dalObject add newValue
        }
        Redirect(indexRoute)
      })
  }

  def edit(id: Long) = Action { implicit request =>
    withExisting(id) { p =>
      Ok(editView(request, p, crudEditForm.fill(p.asNew)))
    }
  }

  def update(id:Long) = Action { implicit request =>
    crudEditForm.bindFromRequest.fold(
      errors => {
        withExisting(id) { p =>
          BadRequest(editView(request, p, errors))
        }
      },
      newPlaceValue => {
        DB.withTransaction { implicit session =>
          dalObject.update(id, newPlaceValue)
        }
        Redirect(indexRoute)
      })
  }
}

