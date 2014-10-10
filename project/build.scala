import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import org.scalatra.sbt._
import sbt.Keys._
import sbt._
import sbtassembly.Plugin._

/* This is the "manifest" for the whole application. Here you specify project wide settings
 * and, more importantly, tell SBT what dependencies your project uses. SBT stands for Simple Build Tool and it's what
 * most Scala projects use as their base. Find out more about how SBT works in the docs http://www.scala-sbt.org/0.12.4/docs/index.html .
 *
 * The only thing you might want to change in here are the libraryDepdencies -- if you want to use another module or dependency you would add it here.
 * */

object ScalatraBuild extends Build {
  val Organization = "com.esports.gtplatform"
  val Name = "gtfest"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.1"
  val ScalatraVersion = "2.3.0"
  val port = SettingKey[Int]("port")
  val Conf = config("container")

  lazy val project = Project (
    "gamefest-platform",
    file("."),
    settings = Defaults.defaultSettings ++ assemblySettings ++ ScalatraPlugin.scalatraWithJRebel ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ scalateSettings ++ Seq(
      port in Conf := 8080,
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers ++= Seq("Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
                        "RoundEights" at "http://maven.spikemark.net/roundeights",
                        Resolver.sonatypeRepo("releases")),
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-compiler" % "2.11.1",
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
        "org.scalatra" %% "scalatra-auth" % "2.3.0",
        "org.scalatra" %% "scalatra-json" % "2.3.0",
        "org.json4s"   %% "json4s-jackson" % "3.2.10",
        "org.json4s" %% "json4s-ext" % "3.2.10",
        "org.json4s" %% "json4s-core" % "3.2.10",
        "com.chuusai" %% "shapeless" % "2.0.0",
        "com.github.julien-truffaut"  %%  "monocle-core" % "0.5.1",
        "com.github.julien-truffaut"  %%  "monocle-generic" % "0.5.1",
        "com.github.julien-truffaut"  %%  "monocle-macro"   % "0.5.1",
        "com.roundeights" %% "mailgun-scala" % "0.2",
        "com.roundeights" %% "scalon" % "0.2",
        "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
        "com.escalatesoft.subcut" %% "subcut" % "2.1",
        "com.typesafe" % "config" % "1.2.1",
        "ch.qos.logback" % "logback-classic" % "1.0.13" % "runtime",
        "org.codehaus.janino" % "janino" % "2.6.1",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container;compile",
        "org.apache.derby" % "derby" % "10.10.1.1",
        "c3p0" % "c3p0" % "0.9.1.2",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
        "com.googlecode.mapperdao" %% "mapperdao" % "1.0.1",
        "mysql" % "mysql-connector-java" % "5.1.18",
        "commons-dbcp" % "commons-dbcp" % "1.4",
        "com.stripe" % "stripe-java" % "1.18.0"
      ),
        mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
        {
            case "spring.tooling" => MergeStrategy.first
            case x => old(x)
        }
        },
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq.empty,  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  )
}
