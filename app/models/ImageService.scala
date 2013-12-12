package models

import org.joda.time.DateTime
import models.dao.ImageRecord

case class Image(url: String, addedAt: DateTime)

trait ImagesComponent {
  val ImageService: ImageService
  
  class ImageService extends BasicServiceOps[Image] {
    object daoMapping {
      import scala.language.implicitConversions

      implicit def mapToLoaded(v: ImageRecord) = Loaded(v.id.get, Image(v.url, v.addedAt))
      implicit def mapToRecord(a: Image) = ImageRecord(None, a.url, a.addedAt)
    }

    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    private val byId = daoService.Images.createFinderBy(_.id)
    
    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)

    def add(image: Image)(implicit session: Session) = daoService.Images.autoInc insert image
  }
}