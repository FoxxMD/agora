import play.PlayScala

name := "play-scala"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

val libraryVersion = "0.4.0"

libraryDependencies ++= Seq(
  jdbc,
  javaJdbc,
  cache,
  ws,
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "com.googlecode.mapperdao" %% "mapperdao" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion
)

resolvers ++= Seq("sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")