package com.esports.gtplatform.business

import com.googlecode.mapperdao.{Entity, Persisted}
import models._
import org.json4s.JsonDSL._
import org.json4s._
/**
 * Created by Matthew on 7/31/2014.
 */
/*
* This is all magic.
* */
class LinkObjectEntitySerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats =>(
  {PartialFunction.empty},{
  case tu: TeamUser =>
    implicit val formats: Formats = DefaultFormats
    ("Team" ->
      ("name" -> tu.team.name) ~
      ("id" -> tu.team.id) ~
      ("resource" -> "/team/") ~
      ("isCaptain" -> tu.isCaptain)) ~
    ("User" ->
      ("name" -> tu.user.globalHandle) ~
      ("id" -> tu.user.id) ~
      ("resource" -> "/user/") ~
      ("isCaptain" -> tu.isCaptain))
}
  ))

class EntitySerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats =>(
  {PartialFunction.empty},{
  case g: Game =>
    implicit val formats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(GameType)
    Extraction.decompose(g.copy())
  case u : User =>
    implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer
    Extraction.decompose(u.copy()) removeField {
      case ("User", _) => true
      case _ => false }
  case t : Team =>
    implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer
    Extraction.decompose(t.copy()) removeField {
      case ("Team", _) => true
      case _ => false }
}

  ))

object GTSerializers {
  val mapperSerializers = List(new LinkObjectEntitySerializer, new EntitySerializer)
}
