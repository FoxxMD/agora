import scala.slick.jdbc.meta.createModel
import scala.slick.codegen.SourceCodeGenerator
import scala.slick.driver.MySQLDriver
import scala.slick.model.Column

/**
 * Created by Matthew on 10/28/2014.
 */
object CustomCodeGenerator {
    def main(args: Array[String]) = {
        val db = MySQLDriver.simple.Database.forURL("jdbc:mysql://localhost/gtgamfest_scal",user="root",password="password",driver="scala.slick.driver.MySQLDriver")
        val excludedTables = Seq("apikeys", "confirmationtokens", "tokens", "passwordtokens", "invites")
        val model = db.withSession { implicit session =>
            val tables = MySQLDriver.getTables.list.filterNot(x => excludedTables contains x.name.name)
            createModel(tables, MySQLDriver)
        }
        val codegen = new SourceCodeGenerator(model) {
            def writeToFile = {
                val packageCode =
                    s"""
                      |package com.esports.gtplatform.dao.slick
                      |
                      |//auto-generated
                      |import com.esports.gtplatform.models._
                      |import models.{EventDetail, _}
                      |
                      |object Tables extends {
                      |val profile = scala.slick.driver.MySQLDriver
                      |} with Tables
                      |
                      |trait Tables extends {
                      |  val profile: scala.slick.driver.JdbcProfile
                      |  import profile.simple._
                      |  ${this.indent(this.code)}
                      |}
                    """.stripMargin

                this.writeStringToFile(
                    packageCode,
                    args(0),
                    "CustomTables",
                    "CustomTables.scala")

            }

            override def entityName = dbTableName => dbTableName match {
                case "nonactiveusers" => "User"
                case "nonactiveuseridentity" => "UserIdentity"
                case _ =>
                    formatEntityName(dbTableName)
            }
            def formatEntityName(str: String): String = {
                str.toLowerCase
                    .split("_")
                    .map{ case "" => "_" case s => s } // avoid possible collisions caused by multiple '_'
                    .map(_.capitalize)
                    .map(x => if(x.charAt(x.length -1) == 's') x.substring(0,x.length-1) else x)
                    .mkString("")
            }

            override def Table = new Table(_) {

                // disable entity class generation and mapping
                override def EntityType = new EntityType {
                    override def enabled = false
                    override def classEnabled = false
                }
                override def Column = new Column(_) { column =>
                    override def rawType = model.name match {
                        case "timeStart" => "org.joda.time.DateTime"
                        case "timeEnd" => "org.joda.time.DateTime"
                        case "createdDate" => "org.joda.time.DateTime"
                        case _ => super.rawType
                    }
                }
            }



            /*        override def code = {
                        super.code + "\n\n" +
                        s"""
                           |
                         """.stripMargin
                    }*/
        }

        codegen.writeToFile

    }



}
