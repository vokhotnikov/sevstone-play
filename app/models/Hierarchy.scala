package models

import scala.annotation.tailrec
import scala.language.postfixOps

trait HierarchicalEntity[NA] extends ModelEntity[NA] {
  def parentId: Option[Long]
  def sortPriority: Long
}

case class Hierarchy[A <: HierarchicalEntity[_]](node: A, parent: Option[A], children: List[Hierarchy[A]])

object Hierarchy {
  def apply[A <: HierarchicalEntity[_]](records: List[A]):List[Hierarchy[A]] = {
    val flattened = records map { Hierarchy[A](_, None, List()) }

    def allNodesIn(subtree: Hierarchy[A]): List[A] = {
      subtree.node :: subtree.children.flatMap { allNodesIn(_) }
    }

    def sortLevel(subtrees: List[Hierarchy[A]]) = subtrees sortBy { _.node.sortPriority }

    def mergeInto(subtree: Hierarchy[A], branches: Map[A, List[Hierarchy[A]]]): Hierarchy[A] = {
      val node = subtree.node
      val myBranches = branches get node
      myBranches match {
        case None => Hierarchy(node, subtree.parent, subtree.children.map{c => mergeInto(c, branches)})
        case Some(b) => Hierarchy(node, subtree.parent,
          sortLevel(b.map{s => Hierarchy(s.node, Some(node), s.children)} ++ subtree.children.map{c => mergeInto(c, branches)}))
      }
    }

    @tailrec
    def mergePass(subtrees: List[Hierarchy[A]]): List[Hierarchy[A]] = {
      val grouped = subtrees groupBy { _.node.parentId }

      val merged = subtrees map { t =>
        val toMerge = allNodesIn(t) flatMap { n =>
          val branch = grouped get Some(n.id)
          branch match {
            case Some(b) => Some(n, b)
            case None => None
          }
        } groupBy { _._1 } mapValues { v => v flatMap {_._2} }

        mergeInto(t, toMerge)
      }

      val nonRootIds = merged flatMap { _.children } flatMap { allNodesIn(_) } map { _.id } toSet
      val filtered = merged filterNot { nonRootIds contains _.node.id }

      if (subtrees.length == filtered.length) filtered else mergePass(filtered)
    }

    mergePass(sortLevel(flattened))
  }
}

