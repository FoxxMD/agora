import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import org.scalatra.sbt._
import sbt.Keys._
import sbt._

object ScalatraBuild extends Build {
    val Organization = "com.esports.gtplatform"
    val Name = "gtfest"
    val Version = "0.1.0-SNAPSHOT"
    val ScalaVersion = "2.11.1"
    val ScalatraVersion = "2.3.0"
    val port = SettingKey[Int]("port")
    val Conf = config("container")

    lazy val project = Project(
        "gamefest-platform",
        file("."),
        settings = Defaults.defaultConfigs ++ ScalatraPlugin.scalatraWithJRebel ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ scalateSettings ++ sbtassembly.Plugin.assemblySettings ++ Seq(
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
                "com.typesafe.slick" %% "slick" % "2.1.0",
                "com.typesafe.slick" %% "slick-codegen" % "2.1.0-RC3",
                "io.strongtyped" %% "active-slick" % "0.2.2",
                "org.json4s" %% "json4s-jackson" % "3.2.10",
                "org.json4s" %% "json4s-ext" % "3.2.10",
                "org.json4s" %% "json4s-core" % "3.2.10",
                "com.chuusai" %% "shapeless" % "2.0.0",
                "com.github.julien-truffaut" %% "monocle-core" % "0.5.1",
                "com.github.julien-truffaut" %% "monocle-generic" % "0.5.1",
                "com.github.julien-truffaut" %% "monocle-macro" % "0.5.1",
                "com.roundeights" %% "mailgun-scala" % "0.2",
                "com.roundeights" %% "scalon" % "0.2",
                "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
                "com.escalatesoft.subcut" %% "subcut" % "2.1",
                "org.scaldi" %% "scaldi" % "0.4",
                "com.typesafe" % "config" % "1.2.1",
                "ch.qos.logback" % "logback-classic" % "1.0.13" % "runtime",
                "org.codehaus.janino" % "janino" % "2.6.1",
                "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container;compile",
                "org.apache.derby" % "derby" % "10.10.1.1",
                "c3p0" % "c3p0" % "0.9.1.2",
                "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile;container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
                "com.googlecode.mapperdao" %% "mapperdao" % "1.0.1",
                "mysql" % "mysql-connector-java" % "5.1.33",
                "commons-dbcp" % "commons-dbcp" % "1.4",
                "com.stripe" % "stripe-java" % "1.18.0"
            ),
                scalateTemplateConfig in Compile <<=(sourceDirectory in Compile) { base =>
                Seq(
                    TemplateConfig(
                        base / "webapp" / "WEB-INF" / "templates",
                        Seq.empty, /* default imports should be added here */
                        Seq.empty, /* add extra bindings here */
                        Some("templates")
                    )
                )
            },
            slick <<= slickCodeGenTask,
            sourceGenerators in Compile <+= slickCodeGenTask
        )
    ).dependsOn(codegenProject)

    lazy val codegenProject = Project(
        id="codegen",
        base=file("codegen"),
        settings = Defaults.defaultConfigs ++ Seq(
            scalaVersion := "2.11.1",
            libraryDependencies ++= List(
                "com.typesafe.slick" %% "slick-codegen" % "2.1.0-RC3"
            )
        )
    )
    // code generation task that calls the customized code generator
    lazy val slick = TaskKey[Seq[File]]("gen-tables")
    lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
        val outputDir = (dir / "slick").getPath // place generated files in sbt's managed sources folder
        toError(r.run("CustomCodeGenerator", cp.files, Array(outputDir), s.log))
        val fname = outputDir + "/CustomTables/CustomTables.scala"
        Seq(file(fname))
    }

}
