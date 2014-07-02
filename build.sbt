import play.Project._

name := "GameFest"

version := "1.0"

playScalaSettings

libraryDependencies++= List(
  javaJdbc,
  "com.googlecode.mapperdao" %% "mapperdao" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.18")