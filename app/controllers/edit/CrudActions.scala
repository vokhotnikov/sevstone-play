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

trait CrudActions[A <: ModelEntity[NA], NA, FormSupportData] { self: Controller =>
  def dalObject: CrudSupport[A, NA]

  def crudEditForm: Form[NA]

  def indexRoute: Call

  def indexView[B](implicit request: Request[B], all: List[A]): Html
  def createView[B](implicit request: Request[B], form: Form[NA], formSupport: FormSupportData): Html
  def editView[B](implicit request: Request[B], a: A, form: Form[NA], formSupport: FormSupportData): Html

  def notFoundErrorText(details: String): String

  def constructFormSupportData(current: Option[A])(implicit session: Session):FormSupportData

  private def withExisting[B](id: Long)(f: A => Result)(implicit request: Request[B]) = {
    DB.withTransaction { implicit session: Session =>
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
    DB.withTransaction { implicit session =>
      Ok(createView(request, crudEditForm, constructFormSupportData(None)))
    }
  }

  def save = Action { implicit request =>
    crudEditForm.bindFromRequest.fold(
      errors => {
        DB.withTransaction { implicit session: Session =>
          BadRequest(createView(request, errors, constructFormSupportData(None)))
        }
      },
      newValue => {
        DB.withTransaction { implicit session: Session =>
          dalObject add newValue
        }
        Redirect(indexRoute)
      })
  }

  def edit(id: Long) = Action { implicit request =>
    withExisting(id) { p =>
      DB.withTransaction { implicit session =>
        Ok(editView(request, p, crudEditForm.fill(p.asNew), constructFormSupportData(Some(p))))
      }
    }
  }

  def update(id:Long) = Action { implicit request =>
    crudEditForm.bindFromRequest.fold(
      errors => {
        withExisting(id) { p =>
          DB.withTransaction { implicit session =>
            BadRequest(editView(request, p, errors, constructFormSupportData(Some(p))))
          }
        }
      },
      newPlaceValue => {
        DB.withTransaction { implicit session: Session =>
          dalObject.update(id, newPlaceValue)
        }
        Redirect(indexRoute)
      })
  }
}

trait SimpleCrudActions[A <: ModelEntity[NA], NA] extends CrudActions[A, NA, Any] { self: Controller =>
  def constructFormSupportData(current: Option[A])(implicit session: Session):Any = None
}

