import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "sevstone"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "com.typesafe.play" %% "play-slick" % "0.4.0",
    "org.scalaz" %% "scalaz-core" % "7.0.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )
}
