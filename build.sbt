import net.litola.SassPlugin

name := "sevstone"

version := "1.0-SNAPSHOT"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.github.nscala-time" %% "nscala-time" % "0.6.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "0.4.0",
  "org.scalaz" %% "scalaz-core" % "7.0.5",
  "com.netflix.rxjava" % "rxjava-scala" % "0.15.1",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)

play.Project.playScalaSettings ++ SassPlugin.sassSettings ++ Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass", "-t", "compressed"))

TaskKey[Unit]("show-user-dir") := println(sys.props("user.dir"))
