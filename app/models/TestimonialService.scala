package models

import play.api.db.slick._
import org.joda.time.DateTime
import models.dao.TestimonialRecord

case class Testimonial(authorName: String, authorEmail: Option[String], text: String, addedAt: DateTime, isApproved: Boolean)

trait TestimonialsComponent {
  val TestimonialService: TestimonialService
  
  class TestimonialService extends BasicServiceOps[Testimonial] {
    object daoMapping {
      import scala.language.implicitConversions
      
      implicit def mapToLoaded(v: TestimonialRecord) = Loaded(v.id.get, Testimonial(v.authorName, v.authorEmail, v.text, v.addedAt, v.isApproved))
      implicit def mapToRecord(t: Testimonial) = TestimonialRecord(None, t.authorName, t.authorEmail, t.text, t.addedAt, t.isApproved)
    }

    import daoMapping._
    import dao.current._
    import models.dao.current.daoService._
    import models.dao.current.daoService.profile.simple._

    def latestTestimonials(limit: Int)(implicit session: Session): List[Loaded[Testimonial]] = {
      Query(daoService.Testimonials).filter(_.isApproved).sortBy(_.addedAt.desc).take(limit).list.map(mapToLoaded)
    }
    
    def allTestimonials(implicit session: Session): List[Loaded[Testimonial]] = {
      Query(daoService.Testimonials).filter(_.isApproved).sortBy(_.addedAt.desc).list.map(mapToLoaded)
    }
    
    private val byId = daoService.Testimonials.createFinderBy(_.id)
    
    def findById(id: Long)(implicit session: Session) = byId(id).firstOption.map(mapToLoaded)
    
    def add(testimonial: Testimonial)(implicit session: Session): Long = {
      daoService.Testimonials.autoInc insert mapToRecord(testimonial)
    }
  }
}