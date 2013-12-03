package models

trait ModelServices extends TestimonialsComponent

object current {
  val Services = new ModelServices {
    val TestimonialService = new TestimonialService()
  }
}