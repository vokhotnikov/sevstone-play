package models

import play.api._
import play.api.test._
import play.api.test.Helpers._

class WithTestDb extends WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))))