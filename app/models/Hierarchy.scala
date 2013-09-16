package models

import scala.annotation.tailrec
import scala.language.postfixOps

trait HierarchicalEntity extends ModelEntity {
  def parentId: Option[Long]
  def sortPriority: Long
}

case class Hierarchy[A <: HierarchicalEntity](node: A, parent: Option[A], children: List[Hierarchy[A]]) {
  def findSubtree(predicate:(A => Boolean)):Option[Hierarchy[A]] = {
    if (predicate(node)) Some(this) else children.flatMap{_.findSubtree(predicate)}.headOption
  }

  def toList:List[A] = {
    node :: children.flatMap { _.toList }
  }
}

object Hierarchy {
  def apply[A <: HierarchicalEntity](records: List[A]):List[Hierarchy[A]] = {
    val flattened = records map { Hierarchy[A](_, None, List()) }

    def sortLevel(subtrees: List[Hierarchy[A]]) = subtrees sortBy { _.node.sortPriority }

    def mergeInto(subtree: Hierarchy[A], branches: Map[Option[Long], List[Hierarchy[A]]]): Hierarchy[A] = {
      val node = subtree.node
      val myBranches = branches.get(node.id)
      myBranches match {
        case None => Hierarchy(node, subtree.parent, subtree.children.map{c => mergeInto(c, branches)})
        case Some(b) => Hierarchy(node, subtree.parent,
          sortLevel(b.map{s => Hierarchy(s.node, Some(node), s.children)} ++ subtree.children.map{c => mergeInto(c, branches)}))
      }
    }

    def mergePass(subtrees: List[Hierarchy[A]]): List[Hierarchy[A]] = {
      subtrees match {
        case Nil => Nil
        case x :: xs => {
          val headIds = x.toList map { n => n.id }
          val toMerge = xs filter { headIds contains _.node.parentId }
          if (toMerge.length > 0) mergePass(mergeInto(x, toMerge.groupBy{_.node.parentId}) :: xs.diff(toMerge))
          else {
            val toJoin = xs.filter{ t =>
              val nodeIds = t.toList map { n => n.id }
              nodeIds contains x.node.parentId
            }.headOption

            toJoin match {
              case None => x :: mergePass(xs)
              case Some(b) => mergePass(mergeInto(b, Map((x.node.parentId, List(x)))) :: xs.diff(List(b)))
            }
          }
        }
      }
    }

    mergePass(sortLevel(flattened))
  }
}

