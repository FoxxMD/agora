import com.tuplejump.sbt.yeoman.Yeoman
import play.PlayScala

name := """play-scala"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.sonatypeRepo("snapshots")

val appDependencies = Seq(
  jdbc,
  javaJdbc,
  cache,
  ws,
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "com.googlecode.mapperdao" %% "mapperdao" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.escalatesoft.subcut" % "subcut_2.10" % "2.1",
  "org.webjars" % "angularjs" % "1.3.0-beta.2"
)

val appSettings = Seq(version := "1.0 Snapshot", libraryDependencies++= appDependencies, scalaVersion := "2.11.1") ++
  Yeoman.yeomanSettings ++
  Yeoman.withTemplates


lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  appSettings: _*
)