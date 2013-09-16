package models

import org.specs2.mutable._

import scalaz._
import Scalaz._

class HierarchySpec extends Specification {
  case class Record(id: Option[Long], parentId: Option[Long], sortPriority: Long, name: String) extends HierarchicalEntity

  "Hierarchy case class" should {
    "return empty list when constructing hierarchy from empty list" in {
      val fromEmpty = Hierarchy.apply[Record](List())
      fromEmpty must be empty
    }

    "return single record as subtree without children and parent" in {
      val r = Record(12l.some, None, 0, "aa")

      val subtrees = Hierarchy(List(r))
      subtrees must contain(Hierarchy(r, None, List())).only
    }

    "return single record as subtree without children and parent even if parentId is set" in {
      val r = Record(12l.some, 100l.some, 0, "aa")

      val subtrees = Hierarchy(List(r))
      subtrees must contain(Hierarchy(r, None, List())).only
    }

    "return two unrelated records as two subtrees without children and parent" in {
      val r1 = Record(12l.some, None, 0, "aa")
      val r2 = Record(100l.some, None, 0, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r1, None, List()), Hierarchy(r2, None, List())).only
    }

    "return two related records as single hierarchy" in {
      val r1 = Record(12l.some, None, 0, "aa")
      val r2 = Record(100l.some, 12l.some, 0, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only

      val subtreesReverse = Hierarchy(List(r2, r1))
      subtreesReverse must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only
    }

    "return two related records as single hierarchy event if parentId is set" in {
      val r1 = Record(12l.some, 200l.some, 0, "aa")
      val r2 = Record(100l.some, 12l.some, 0, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only

      val subtreesReverse = Hierarchy(List(r2, r1))
      subtreesReverse must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only
    }

    "return children in correct order" in {
      var r = Record(10l.some, None, 300, "root")
      val r1 = Record(12l.some, 10l.some, 200, "aa")
      val r2 = Record(100l.some, 10l.some, 100, "bb")

      val subtrees = Hierarchy(List(r1, r2, r))
      subtrees must contain(Hierarchy(r, None, List(
        Hierarchy(r2, Some(r), List()), Hierarchy(r1, Some(r), List())))).inOrder

      val subtreesReverse = Hierarchy(List(r, r2, r1))
      subtreesReverse must contain(Hierarchy(r, None, List(
        Hierarchy(r2, Some(r), List()), Hierarchy(r1, Some(r), List())))).inOrder
    }

    "return parents in correct order" in {
      val r1 = Record(12l.some, None, 200, "aa")
      val r2 = Record(100l.some, None, 100, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r2, None, List()), Hierarchy(r1, None, List())).inOrder

      val subtreesReverse = Hierarchy(List(r2, r1))
      subtreesReverse must contain(Hierarchy(r2, None, List()), Hierarchy(r1, None, List())).inOrder
    }

    "support multi-level hierarchy" in {
      var r = Record(10l.some, None, 300, "root")
      val r1 = Record(12l.some, 10l.some, 200, "aa")
      val r2 = Record(100l.some, 12l.some, 100, "bb")

      val subtrees = Hierarchy(List(r1, r2, r))
      subtrees must contain(Hierarchy(r, None, List(
        Hierarchy(r1, Some(r), List(Hierarchy(r2, Some(r1), List())))))).inOrder

      val subtreesReverse = Hierarchy(List(r, r2, r1))
      subtreesReverse must contain(Hierarchy(r, None, List(
        Hierarchy(r1, Some(r), List(Hierarchy(r2, Some(r1), List())))))).inOrder
    }

    "return subtree by predicate" in {
      val r = Record(10l.some, None, 1, "root");
      val r1 = Record(20l.some, 10l.some, 1, "subtree");
      val r2 = Record(30l.some, 20l.some, 1, "leaf");
      val r3 = Record(25l.some, 10l.some, 3, "subleaf")

      val tree = Hierarchy(List(r, r1, r2, r3)).head

      tree.findSubtree(n => false) must_== None
      tree.findSubtree(n => true) must_== Some(tree)
      tree.findSubtree(n => n == r) must_== Some(tree)
      tree.findSubtree(n => n == r1) must_== Some(tree.children.head)
      tree.findSubtree(n => n == r2) must_== Some(tree.children.head.children.head)

      tree.findSubtree(n => n == r3).map{_.node} must_== Some(r3)
    }
  }
}
