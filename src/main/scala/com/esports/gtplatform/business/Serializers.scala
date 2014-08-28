package com.esports.gtplatform.business

import com.googlecode.mapperdao.{Entity, Persisted}
import models._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
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
  case eu: EventUser =>
    implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all
      ("name" -> eu.event.name) ~
      ("id" -> eu.event.id) ~
      ("date" -> Extraction.decompose(eu.event.details.timeStart)) ~
      ("isPresent" -> eu.isPresent) ~
      ("resource" -> "/event/")
  case tt: TournamentTeam =>
    implicit val formats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(BracketType)
    ("Tournament" ->
      ("name" -> tt.tournament.details.name) ~
      ("bracketType" -> Extraction.decompose(tt.tournament.bracketType)) ~
        ("game" ->
          ("name" -> tt.tournament.game.name) ~
          ("id" -> tt.tournament.game.id) ~
          ("resource" -> "/game/"))) ~
      ("Team" ->
        ("name" -> tt.team.name ) ~
        ("id" -> tt.team.id) ~
        ("isPresent" -> tt.isPresent) ~
        ("resource" -> "/team/"))
  case tu: TournamentUser =>
    implicit val formats: Formats = DefaultFormats +  new org.json4s.ext.EnumNameSerializer(BracketType)
    ("Tournament" ->
      ("name" -> tu.tournament.details.name) ~
        ("bracketType" -> Extraction.decompose(tu.tournament.bracketType)) ~
        ("game" ->
          ("name" -> tu.tournament.game.name) ~
            ("id" -> tu.tournament.game.id) ~
            ("resource" -> "/game/"))) ~
      ("User" ->
        ("name" -> tu.user.globalHandle ) ~
          ("id" -> tu.user.id) ~
          ("isPresent" -> tu.isPresent) ~
          ("resource" -> "/user/"))
}
  ))

class EntitySerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats =>(
  {PartialFunction.empty},{
  case g: Game =>
    implicit val formats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(GameType) ++ org.json4s.ext.JodaTimeSerializers.all
    Extraction.decompose(g.copy())
  case u : User =>
    implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new org.json4s.ext.EnumNameSerializer(BracketType) ++ org.json4s.ext.JodaTimeSerializers.all
    Extraction.decompose(u.copy()) removeField {
      case ("User", _) => true
      case _ => false }
  case t : Team =>
    implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new org.json4s.ext.EnumNameSerializer(BracketType) ++ org.json4s.ext.JodaTimeSerializers.all
    /*merge
      render("captain" -> t.getCaptain.globalHandle)*/
    Extraction.decompose(t.copy()) merge
      render("captain" -> t.getCaptain.globalHandle) removeField {
      case ("Team", _) => true
      case _ => false }
  case e: Event =>
    implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new org.json4s.ext.EnumNameSerializer(JoinType) ++ org.json4s.ext.JodaTimeSerializers.all
    Extraction.decompose(e.copy()).replace(List("users"), e.users.size)
    //TODO prevent users from being serialized. For basic info that is way too much data and should be its own request.
  case t: Tournament =>
    implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new org.json4s.ext.EnumNameSerializer(JoinType) + new org.json4s.ext.EnumNameSerializer(BracketType) ++ org.json4s.ext.JodaTimeSerializers.all
    Extraction.decompose(t.copy())
      .replace(List("users"),t.users.size)
      .replace(List("teams"),t.teams.size)
      .removeField {
      case ("Tournament", _) => true
      case _ => false }
}
  ))

object GTSerializers {
  val mapperSerializers = List(new LinkObjectEntitySerializer, new EntitySerializer)
}
