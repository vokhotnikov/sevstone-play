package controllers.edit

import play.api.mvc.{Controller,Request}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import models._

import views.html.edit.pickoutplaces

object PickOutPlacesController extends Controller with SimpleCrudActions[PickOutPlace] {
  val pickOutPlaceForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> text,
      "id" -> optional(longNumber)
    )(PickOutPlace.apply)(PickOutPlace.unapply)
  )

  def dalObject = PickOutPlaces

  def crudEditForm = pickOutPlaceForm

  def indexRoute = routes.PickOutPlacesController.index

  def indexView[B](implicit request: Request[B], all:List[PickOutPlace]) = pickoutplaces.index(all)
  def createView[B](implicit request: Request[B], form: Form[PickOutPlace], formSupport: Any) = pickoutplaces.create(form)
  def editView[B](implicit request: Request[B], a: PickOutPlace, form: Form[PickOutPlace], formSupport: Any) = pickoutplaces.edit(a, form)

  def notFoundErrorText(details: String) = "Место отбора не найдено: " + details
}
