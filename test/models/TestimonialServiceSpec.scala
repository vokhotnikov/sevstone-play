package models

import org.specs2.mutable._
import models.dao.current.daoService
import org.specs2.specification.Scope
import com.github.nscala_time.time.Imports._

import daoService.profile.simple._

import play.api.db.slick.DB

class TestimonialServiceSpec extends Specification {
  "testimonial service" should {
    "return latest testimonials" in new WithTestDb with TestData {
      DB.withSession { implicit s: Session =>
        service.latestTestimonials(10) must be empty
    
        val id1 = service add t1
        val id3 = service add t3
        
        service.latestTestimonials(2) must_== List(Loaded(id1, t1), Loaded(id3, t3))
        service.latestTestimonials(3) must_== List(Loaded(id1, t1), Loaded(id3, t3))
        
        service add (t2.copy(isApproved = false))
        service.latestTestimonials(3) must_== List(Loaded(id1, t1), Loaded(id3, t3))
        
        val id2 = service add t2
        
        service.latestTestimonials(2) must_== List(Loaded(id1, t1), Loaded(id2, t2))
        service.latestTestimonials(3) must_== List(Loaded(id1, t1), Loaded(id2, t2), Loaded(id3, t3))
      }
    }
  }
  
  trait TestData extends TestimonialsComponent { this: Scope =>
    val TestimonialService = new TestimonialService
    
    val service = TestimonialService

    val t1 = new Testimonial("Vasya Pupkin", Some("vasya@pupkin.com"), "great site!", DateTime.now, true)
    val t2 = new Testimonial("Glafira Kukushkina", None, "Never saw anything like that", DateTime.now - 2.months, true)
    val t3 = new Testimonial("Robot", Some("robot@mail.mail.mail"), "beep-beep", DateTime.now - 9.weeks, true)
  }
}