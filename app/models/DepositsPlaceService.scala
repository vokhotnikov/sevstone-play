package models

import models.dao.DepositsPlaceRecord

case class DepositsPlace(title: String, description: String)

trait DepositsPlacesComponent {
  class DepositsPlaceService extends BasicServiceOps[DepositsPlace] {
    object daoMapping {
      import scala.language.implicitConversions

      implicit def mapToLoaded(v: DepositsPlaceRecord) = Loaded(v.id.get, DepositsPlace(v.title, v.description))
      implicit def mapToRecord(t: DepositsPlace) = DepositsPlaceRecord(None, t.title, t.description)
    }

    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    private val byId = daoService.DepositsPlaces.createFinderBy(_.id)

    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)

    def add(testimonial: DepositsPlace)(implicit session: Session): Long = {
      daoService.DepositsPlaces.autoInc insert mapToRecord(testimonial)
    }
  }
}