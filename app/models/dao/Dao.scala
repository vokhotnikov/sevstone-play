package models.dao

import slick.driver.ExtendedProfile
import play.api.db.slick.Profile
import play.api.db.slick.DB

class Dao(override val profile: ExtendedProfile)
  extends TestimonialComponent
  with ArticleComponent
  with ImageComponent
  with Profile {

  val Testimonials = new Testimonials
  val Articles = new Articles
  val Images = new Images
}

object current {
  val daoService = new Dao(DB(play.api.Play.current).driver)
}