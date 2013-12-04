package models

import play.api.db.slick._
import models.dao.CategoryRecord

case class Category(parent: Option[MaybeLoaded[Category]], title: String, isHidden: Boolean, sortPriority: Long)

trait CategoriesComponent {
  val CategoryService: CategoryService

  class CategoryService extends BasicServiceOps[Category] {
    object daoMapping {
      import scala.language.implicitConversions

      implicit def mapToLoaded(v: CategoryRecord)(implicit session: Session): Loaded[Category] = {
        val parent: Option[MaybeLoaded[Category]] = v.parentId.map(i => CategoryService.findById(i).get)
        Loaded(v.id.get, Category(parent, v.title, v.isHidden, v.sortPriority))
      }
      implicit def mapToRecord(c: Category)(implicit session: Session) =
        CategoryRecord(None, c.parent.map(CategoryService.addIfNeeded), c.title, c.isHidden, c.sortPriority)
    }

    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    private val byId = daoService.Categories.createFinderBy(_.id)

    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)

    def add(category: Category)(implicit session: Session) = daoService.Categories.autoInc insert category
  }
}