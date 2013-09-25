package controllers.edit

import play.api.mvc.{Controller,Request}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import models._

import views.html.edit.depositsplaces

object DepositsPlacesController extends Controller with SimpleCrudActions[DepositsPlace] with securesocial.core.SecureSocial {
  val depositsPlaceForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> text,
      "id" -> optional(longNumber)
    )(DepositsPlace.apply)(DepositsPlace.unapply)
  )

  def dalObject = DepositsPlaces

  def crudEditForm = depositsPlaceForm

  def indexRoute = routes.DepositsPlacesController.index

  def indexView[B](implicit request: Request[B], all:List[DepositsPlace]) = depositsplaces.index(all)
  def createView[B](implicit request: Request[B], form: Form[DepositsPlace], formSupport: Any) = depositsplaces.create(form)
  def editView[B](implicit request: Request[B], a: DepositsPlace, form: Form[DepositsPlace], formSupport: Any) = depositsplaces.edit(a, form)

  def notFoundErrorText(details: String) = "Месторождение не найдено: " + details
}
