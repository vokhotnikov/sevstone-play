package models

import org.specs2.mutable._

class HierarchySpec extends Specification {
  case class NewRecord(name: String)
  case class Record(id: Long, parentId: Option[Long], sortPriority: Long, name: String) extends HierarchicalEntity[NewRecord] {
    def asNew = NewRecord(name)
  }

  "Hierarchy case class" should {
    "return empty list when constructing hierarchy from empty list" in {
      val fromEmpty = Hierarchy.apply[Record](List())
      fromEmpty must be empty
    }

    "return single record as subtree without children and parent" in {
      val r = Record(12, None, 0, "aa")

      val subtrees = Hierarchy(List(r))
      subtrees must contain(Hierarchy(r, None, List())).only
    }

    "return single record as subtree without children and parent even if parentId is set" in {
      val r = Record(12, Some(100), 0, "aa")

      val subtrees = Hierarchy(List(r))
      subtrees must contain(Hierarchy(r, None, List())).only
    }

    "return two unrelated records as two subtrees without children and parent" in {
      val r1 = Record(12, None, 0, "aa")
      val r2 = Record(100, None, 0, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r1, None, List()), Hierarchy(r2, None, List())).only
    }

    "return two related records as single hierarchy" in {
      val r1 = Record(12, None, 0, "aa")
      val r2 = Record(100, Some(12), 0, "bb")

      println("!-!")
      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only

      val subtreesReverse = Hierarchy(List(r2, r1))
      subtreesReverse must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only
    }

    "return two related records as single hierarchy event if parentId is set" in {
      val r1 = Record(12, Some(200), 0, "aa")
      val r2 = Record(100, Some(12), 0, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only

      val subtreesReverse = Hierarchy(List(r2, r1))
      subtreesReverse must contain(Hierarchy(r1, None, List(Hierarchy(r2, Some(r1), List())))).only
    }

    "return children in correct order" in {
      var r = Record(10, None, 300, "root")
      val r1 = Record(12, Some(10), 200, "aa")
      val r2 = Record(100, Some(10), 100, "bb")

      val subtrees = Hierarchy(List(r1, r2, r))
      subtrees must contain(Hierarchy(r, None, List(
        Hierarchy(r2, Some(r), List()), Hierarchy(r1, Some(r), List())))).inOrder

      val subtreesReverse = Hierarchy(List(r, r2, r1))
      subtreesReverse must contain(Hierarchy(r, None, List(
        Hierarchy(r2, Some(r), List()), Hierarchy(r1, Some(r), List())))).inOrder
    }

    "return parents in correct order" in {
      val r1 = Record(12, None, 200, "aa")
      val r2 = Record(100, None, 100, "bb")

      val subtrees = Hierarchy(List(r1, r2))
      subtrees must contain(Hierarchy(r2, None, List()), Hierarchy(r1, None, List())).inOrder

      val subtreesReverse = Hierarchy(List(r2, r1))
      subtreesReverse must contain(Hierarchy(r2, None, List()), Hierarchy(r1, None, List())).inOrder
    }

  }
}