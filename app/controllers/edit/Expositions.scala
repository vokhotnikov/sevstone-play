package controllers.edit

import play.api.mvc.{Controller,Request,Action}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import models._

import views.html.edit.expositions

object ExpositionsController extends Controller with CrudActions[Exposition, List[Exposition]] with securesocial.core.SecureSocial {
  val expositionForm = Form(
    mapping(
      "parentId" -> optional(longNumber),
      "title" -> nonEmptyText,
      "description" -> text,
      "sortPriority" -> longNumber,
      "id" -> optional(longNumber)
    )(Exposition.apply)(Exposition.unapply)
  )

  def dalObject = Expositions

  def crudEditForm = expositionForm

  def constructFormSupportData(current: Option[Exposition])(implicit session:Session) = {
    val all = Expositions.findAll
    current match {
      case Some(c) => {
        all.diff(Hierarchy(all).flatMap{ t =>
          val subtree = t.findSubtree{_.id == c.id}
          subtree match {
            case None => Nil
            case Some(st) => st.toList
          }
        })
      }
      case None => all
    }
  }

  def indexRoute = routes.ExpositionsController.indexTree

  def indexView[B](implicit request: Request[B], all:List[Exposition]) = expositions.index(all)
  def createView[B](implicit request: Request[B], form: Form[Exposition], formSupport: List[Exposition]) = expositions.create(form, formSupport)
  def editView[B](implicit request: Request[B], a: Exposition, form: Form[Exposition], formSupport: List[Exposition]) = expositions.edit(a, form, formSupport)
  def notFoundErrorText(details: String) = "Экспозиция не найдена: " + details

  def indexTree = SecuredAction { implicit request =>
    DB.withTransaction { implicit session =>
      val expos = Expositions.loadHierarchies
      Ok(expositions.indexTree(expos))
    }
  }
}
