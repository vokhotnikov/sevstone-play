package controllers.edit

import play.api.mvc.{Controller,Request,Action}
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import models._

import views.html.edit.categories

object CategoriesController extends Controller with CrudActions[Category, NewCategory, List[Category]] {
  val expositionForm = Form(
    mapping(
      "parentId" -> optional(longNumber),
      "title" -> nonEmptyText,
      "isHidden" -> boolean,
      "sortPriority" -> longNumber
    )(NewCategory.apply)(NewCategory.unapply)
  )

  def dalObject = Categories

  def crudEditForm = expositionForm

  def constructFormSupportData(current: Option[Category])(implicit session:Session) = {
    val all = Categories.findAll
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

  def indexRoute = routes.CategoriesController.indexTree

  def indexView[B](implicit request: Request[B], all:List[Category]) = categories.index(all)
  def createView[B](implicit request: Request[B], form: Form[NewCategory], formSupport: List[Category]) = categories.create(form, formSupport)
  def editView[B](implicit request: Request[B], a: Category, form: Form[NewCategory], formSupport: List[Category]) = categories.edit(a, form, formSupport)
  def notFoundErrorText(details: String) = "Категория не найдена: " + details

  def indexTree = Action { implicit request =>
    DB.withTransaction { implicit session =>
      val expos = Categories.loadHierarchies
      Ok(categories.indexTree(expos))
    }
  }
}
