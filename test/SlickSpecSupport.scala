import play.api.test._
import play.api.test.Helpers._

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._

import models._

object SlickSpecSupport {
  def memDB[T](code: Session=>T) =
      running( FakeApplication( additionalConfiguration = inMemoryDatabase() ++ Map(
        "db.default.driver" -> "org.h2.Driver",
        "db.default.url"    -> "jdbc:h2:mem:test;MODE=PostgreSQL",
        "evolutionplugin"   -> "disabled"
      ) ) ) {
        val ddl = PickOutPlaces.ddl

        play.api.db.slick.DB.withSession { implicit session: Session =>
          try {
            ddl.create
            code(session)
          } finally {
            ddl.drop
          }
        }
      }
}
