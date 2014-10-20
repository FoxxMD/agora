package com.esports.gtplatform.business

import com.escalatesoft.subcut.inject.{AutoInjectable, Injectable, BindingModule}
import com.esports.gtplatform.business.services.TeamService
import com.esports.gtplatform.json.DateSerializer
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.{Entity, Persisted}
import models._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by Matthew on 7/31/2014.
 */
/*
* This is all magic.
* */

class EntityDetailsSerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats => ( {
    PartialFunction.empty
}, {
    case ed: EventDetails =>
        implicit val formats: Formats = DefaultFormats + new DateSerializer
        val f = Extraction.decompose(ed.copy())
        if (ed.scheduledEvents.isDefined) {
            f.replace(List("scheduledEvents"), parseOpt(ed.scheduledEvents.get))
        }
        else
            f
    case td: TournamentDetails =>
        implicit val formats: Formats = DefaultFormats + new DateSerializer
        Extraction.decompose(td.copy())
    case up: UserPlatformProfile =>
        implicit val formats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(GamePlatform)
        Extraction.decompose(up.copy())
    case tt: TournamentType =>
        implicit val formats: Formats = DefaultFormats
        Extraction.decompose(tt.copy())
}
    ))

class EntityAuxillarySerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats => ( {
    PartialFunction.empty
}, {
    case ep: EventPayment =>
        implicit val formats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(PaymentType)
        Extraction.decompose(ep.copy()) removeField {
            case ("secretKey", _) => true
            case _ => false
        }
    case g: Game =>
        implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer
        Extraction.decompose(g.copy())
    case t: Team =>
        implicit val formats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(JoinType) + new LinkObjectEntitySerializer
        Extraction.decompose(t.copy())
    /*    Extraction.decompose(t.copy()) merge
          render(("userPlay" -> t.tournament.tournamentType.userPlay) ~
                 ("teamPlay" -> t.tournament.tournamentType.teamPlay))*/
}
    ))

class LinkObjectEntitySerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats => ( {
    PartialFunction.empty
}, {
    case tu: GuildUser =>
        implicit val formats: Formats = DefaultFormats
        ("Guild" ->
            ("name" -> tu.guild.name) ~
                ("id" -> tu.guild.id) ~
                ("isCaptain" -> tu.isCaptain)) ~
            ("User" ->
                ("name" -> tu.user.globalHandle) ~
                    ("id" -> tu.user.id) ~
                    ("isCaptain" -> tu.isCaptain))
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
        implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer + new EntitySerializer
        ("event" ->
            ("name" -> eu.event.name) ~
            ("id" -> eu.event.id)) ~
            //("date" -> Extraction.decompose(eu.event.details.timeStart)) ~
            ("isPresent" -> eu.isPresent) ~
            ("isAdmin" -> eu.isAdmin) ~
            ("isModerator" -> eu.isModerator) ~
            ("hasPaid" -> eu.hasPaid) ~
            ("globalHandle" -> eu.user.globalHandle) ~
            ("id" -> eu.user.id) ~
            ("platforms" -> Extraction.decompose(eu.user.gameProfiles)) ~
            ("guilds" -> Extraction.decompose(eu.user.guilds)) ~
            ("tournaments" -> eu.user.getAssociatedTournaments(new TournamentUserRepository, new TeamUserRepository, new TournamentRepository, Option(eu.event)).size)//TODO clean this shit up
    case tu: TournamentUser =>
        implicit val formats: Formats = DefaultFormats + new EntityDetailsSerializer + new EntityAuxillarySerializer
        ("name" -> tu.user.globalHandle) ~
            ("id" -> tu.user.id) ~
            ("isPresent" -> tu.isPresent) ~
            ("isModerator" -> tu.isModerator) ~
            ("isAdmin" -> tu.isAdmin) ~
            ("resource" -> "/user/") ~
            ("Tournament" ->
                ("name" -> tu.tournament.details.flatMap(x => x.name)) ~
                    ("tournamentType" -> Extraction.decompose(tu.tournament.tournamentType)) ~
                    ("id" -> tu.tournament.id) ~
                    ("game" -> Extraction.decompose(tu.tournament.game)
                        /*            ("name" -> tu.tournament.game.name) ~
                                        ("id" -> tu.tournament.game.id)*/))
}
    ))

//[T](mt: Manifest[T](implicit val bindingModule: BindingModule) https://github.com/dickwall/subcut/blob/master/GettingStarted.md#creating-an-injectable-class ?
class EntitySerializer[T: Manifest] extends CustomSerializer[Entity[Int, Persisted, Class[T]]](formats => ( {
    PartialFunction.empty
}, {
    case u: User =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer
        Extraction.decompose(u.copy()) removeField {
            case ("User", _) => true
            case("email", _) => true
            case _ => false
        } merge render(
            //TODO Get rid of nasty coupling to repository implementation. How to mix in Injectable?
            ("events" -> Extraction.decompose(u.getAssociatedEvents(new EventUserRepository))) ~
                ("tournaments" -> {
                    val tRepo = new TeamRepository
                    /*Extraction.decompose(u.getAssociatedTournaments(new TournamentUserRepository))*/
                    val utour = u.getAssociatedTournaments(new TournamentUserRepository, new TeamUserRepository, new TournamentRepository)

                    utour.map(x =>
                        ("id" -> x.id) ~
                        ("name" -> x.details.flatMap(u => u.name)) ~
                        ("game" -> x.game.name) ~
                        ("tournamentType" -> x.tournamentType.name) ~
                        ("eventId" -> x.event.id) ~
                        ("teamPlay" -> x.tournamentType.teamPlay) ~
                        ("users" -> x.users.size) ~
                        ("teams" -> x.teams.size))
                })
        )
    case t: Guild =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new EntityDetailsSerializer + new org.json4s.ext.EnumNameSerializer(JoinType) ++ org.json4s.ext.JodaTimeSerializers.all + new EntityAuxillarySerializer
        Extraction.decompose(t.copy()) merge
            render("captain" -> t.getCaptain.globalHandle) removeField {
            case ("Team", _) => true
            case _ => false
        }
    /* merge
      render("tournaments" -> Extraction.decompose(teamRepo.getByTeam(t))) merge
      render("events" -> Extraction.decompose(teamRepo.getByTeam(t).map(x => x.tournament.event).distinct.map(u => ("name" -> u.name) ~ ("id" -> u.id))))*/
    case e: Event =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new org.json4s.ext.EnumNameSerializer(JoinType) ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer + new EntityAuxillarySerializer
        (Extraction.decompose(e.copy()).replace(List("users"), e.users.size) merge
            render("admins" -> e.getAdmins.map(x => ("Name" -> x.globalHandle) ~ ("id" -> x.id))))
            .replace(List("payments"), Extraction.decompose(e.payments.filter(x => x.isEnabled))) //TODO learn json4s and remove non enabled events from JSON rather than re-rendering filtered list
            .replace(List("tournaments"), e.tournaments.size) merge
            render("games" -> e.tournaments.map(x => x.game).groupBy(m => m)
                .map(y => ("name" -> y._1.name) ~
                ("id" -> y._1.id) ~
                ("gameType" -> Extraction.decompose(y._1.gameType)) ~
                ("count" -> y._2.size)
                )
            ) merge
            render("teams" -> e.tournaments.foldLeft(0)((b, a) => b + a.teams.size))
    case t: Tournament =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new org.json4s.ext.EnumNameSerializer(JoinType) ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer + new EntityAuxillarySerializer
        val tour = Extraction.decompose(t.copy())
            .replace(List("users"), t.users.size)
            .replace(List("teams"), t.teams.size)
            .removeField {
            case ("Tournament", _) => true
            case _ => false
        }
        tour
}
    ))

object GTSerializers {
    val mapperSerializers = List(new LinkObjectEntitySerializer, new EntityDetailsSerializer, new com.esports.gtplatform.json.DateSerializer, new EntityAuxillarySerializer)
}
