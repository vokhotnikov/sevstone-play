package models

import scala.annotation.tailrec
import scala.language.postfixOps

trait HierarchyElementLike[A] {
  def id(from: A): Long
  def parentId(from: A): Option[Long]
  def comesBefore(me: A, other: A): Boolean
}

case class Hierarchy[A](node: A, parent: Option[A], children: List[Hierarchy[A]]) {
  def findSubtree(predicate:(A => Boolean)):Option[Hierarchy[A]] = {
    if (predicate(node)) Some(this) else children.flatMap{_.findSubtree(predicate)}.headOption
  }

  def toList:List[A] = {
    node :: children.flatMap { _.toList }
  }
  
  def toListWithDepth(initialDepth: Int = 0): List[(A, Int)] = {
    (node, initialDepth) :: children.flatMap { _.toListWithDepth(initialDepth + 1) }
  }
}

object Hierarchy {
  def apply[A](records: List[A])(implicit hierarchyOps: HierarchyElementLike[A]):List[Hierarchy[A]] = {
    val flattened = records map { Hierarchy[A](_, None, List()) }

    def sortLevel(subtrees: List[Hierarchy[A]]) = subtrees.sortWith((x, y) => hierarchyOps.comesBefore(x.node, y.node))

    def mergeInto(subtree: Hierarchy[A], branches: Map[Option[Long], List[Hierarchy[A]]]): Hierarchy[A] = {
      val node = subtree.node
      val myBranches = branches.get(Some(hierarchyOps.id(node)))
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
          val headIds = x.toList map { n => Some(hierarchyOps.id(n)) }
          val toMerge = xs filter { el => headIds contains hierarchyOps.parentId(el.node) }
          if (toMerge.length > 0) mergePass(mergeInto(x, toMerge.groupBy{ el => hierarchyOps.parentId(el.node)}) :: xs.diff(toMerge))
          else {
            val toJoin = xs.filter{ t =>
              val nodeIds = t.toList map { n => Some(hierarchyOps.id(n)) } 
              nodeIds contains hierarchyOps.parentId(x.node)
            }.headOption

            toJoin match {
              case None => x :: mergePass(xs)
              case Some(b) => mergePass(mergeInto(b, Map((hierarchyOps.parentId(x.node), List(x)))) :: xs.diff(List(b)))
            }
          }
        }
      }
    }

    sortLevel(mergePass(flattened))
  }
}

