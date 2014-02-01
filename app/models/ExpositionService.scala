package models

import play.api.db.slick._
import models.dao.ExpositionRecord

case class Exposition(parent: Option[MaybeLoaded[Exposition]], title: String, description: String, sortPriority: Long)

object ExpositionImplicits {
  implicit object ExpositionHierarchyElementLike extends HierarchyElementLike[Loaded[Exposition]] {
    def id(from: Loaded[Exposition]) = from.id
    def parentId(from: Loaded[Exposition]) = from.value.parent.flatMap {
      case p: Loaded[Exposition] => Some(p.id)
      case _ => None
    }
    def comesBefore(me: Loaded[Exposition], other: Loaded[Exposition]) = {
      import scala.math.Ordering.Implicits._

      def asTuple(l: Loaded[Exposition]) = (l.value.sortPriority, l.value.title, l.id)

      asTuple(me) < asTuple(other)
    }
  }
}

trait ExpositionsComponent {
  import ExpositionImplicits._
  
  val ExpositionService: ExpositionService

  class ExpositionService extends BasicServiceOps[Exposition] {
    object daoMapping {
      import scala.language.implicitConversions

      implicit def mapToLoaded(v: ExpositionRecord)(implicit session: Session): Loaded[Exposition] = {
        val parent: Option[MaybeLoaded[Exposition]] = v.parentId.map(i => ExpositionService.findById(i).get)
        Loaded(v.id.get, Exposition(parent, v.title, v.description, v.sortPriority))
      }
      implicit def mapToRecord(c: Exposition)(implicit session: Session) =
        ExpositionRecord(None, c.parent.map(ExpositionService.addIfNeeded), c.title, c.description, c.sortPriority)
    }

    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    private val byId = daoService.Expositions.createFinderBy(_.id)

    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)

    def add(exposition: Exposition)(implicit session: Session) = daoService.Expositions.autoInc insert exposition

    def loadAllTrees(implicit session: Session) = {
      val elems = Query(daoService.Expositions).list.map(mapToLoaded) 
      Hierarchy(elems)
    }
    
    def findSubtree(fromId: Long)(implicit session: Session) = {
      loadAllTrees.flatMap(h => h.findSubtree(_.id == fromId)).headOption
    }
  }
}