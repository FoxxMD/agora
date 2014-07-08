name := """play-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  javaJdbc,
  cache,
  ws,
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "com.googlecode.mapperdao" %% "mapperdao" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.18"
)

resolvers += "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"