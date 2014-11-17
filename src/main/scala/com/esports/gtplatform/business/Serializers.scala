package com.esports.gtplatform.business

import com.esports.gtplatform.json.DateSerializer
import com.esports.gtplatform.models.Team
import models._
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by Matthew on 7/31/2014.
 */


class EntityDetailsSerializer[T: Manifest] extends CustomSerializer[Class[T]](formats => ( {
    PartialFunction.empty
}, {
    case ed: EventDetail =>
        implicit val formats: Formats = DefaultFormats + new DateSerializer
        val f = Extraction.decompose(ed)
        if (ed.scheduledEvents.isDefined) {
            f.replace(List("scheduledEvents"), parseOpt(ed.scheduledEvents.get))
        }
        else
            f
    case td: TournamentDetail =>
        implicit val formats: Formats = DefaultFormats + new DateSerializer
        var f = Extraction.decompose(td)
        if (td.rules.isDefined) {
            f = f.replace(List("rules"), parseOpt(td.rules.get))
        }
        f
}
    ))

class EntityAuxillarySerializer[T: Manifest] extends CustomSerializer[Class[T]](formats => ( {
    PartialFunction.empty
}, {
    case ep: EventPayment =>
        implicit val formats: Formats = DefaultFormats
        Extraction.decompose(ep) removeField {
            case ("secretKey", _) => true
            case _ => false
        }
    case t: Team =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer
        Extraction.decompose(t) merge
            render("teamPlayers" -> Extraction.decompose(t.teamPlayers)) removeField{
            case ("email", _) => true
            case ("team", _) => true
            case _ => false
        }
    /*    Extraction.decompose(t.copy()) merge
          render(("userPlay" -> t.tournament.tournamentType.userPlay) ~
                 ("teamPlay" -> t.tournament.tournamentType.teamPlay))*/
}
    ))

class LinkObjectEntitySerializer[T: Manifest] extends CustomSerializer[Class[T]](formats => ( {
    PartialFunction.empty
}, {
    case tu: GuildUser =>
        implicit val formats: Formats = DefaultFormats + new DateSerializer
        Extraction.decompose(tu) merge
            render(("user" -> Extraction.decompose(tu.user)) ~
                ("guild" -> Extraction.decompose(tu.guild)))
    case tu: TeamUser =>
        implicit val formats: Formats = DefaultFormats
        Extraction.decompose(tu) merge
            render(("user" -> Extraction.decompose(tu.user)) ~
                ("team" -> Extraction.decompose(tu.team)))
    /*        ("Team" ->
                ("name" -> tu.teamId.name) ~
                    ("id" -> tu.teamId.id) ~
                    ("resource" -> "/team/") ~
                    ("isCaptain" -> tu.isCaptain)) ~
                ("User" ->
                    ("name" -> tu.userId.globalHandle) ~
                        ("id" -> tu.userId.id) ~
                        ("resource" -> "/user/") ~
                        ("isCaptain" -> tu.isCaptain))*/
/*    case eu: EventUser =>
        implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer //+ new EntitySerializer
        Extraction.decompose(eu) removeField {
            case ("customerId", _) => true
            case ("recieptId", _) => true
            case _ => false
        } merge
            render(("event" -> Extraction.decompose(eu.event)) ~
                ("user" -> Extraction.decompose(eu.user))) removeField {
            case ("admins", _) => true
        case("moderators", _) => true
        case("payments", _) => true
        case("games", _) => true
            case _ => false
        }*/
    case tu: TournamentUser =>
        implicit val formats: Formats = DefaultFormats + new EntityDetailsSerializer + new EntityAuxillarySerializer
        Extraction.decompose(tu) merge
            render(("tournament" -> Extraction.decompose(tu.tournament)) ~
                ("user" -> Extraction.decompose(tu.user)))
    /*        ("name" -> tu.userId.globalHandle) ~
                ("id" -> tu.userId.id) ~
                ("isPresent" -> tu.isPresent) ~
                ("isModerator" -> tu.isModerator) ~
                ("isAdmin" -> tu.isAdmin) ~
                ("resource" -> "/user/") ~
                ("Tournament" ->
                    ("name" -> tu.tournamentId.details.flatMap(x => x.name)) ~
                        ("tournamentType" -> Extraction.decompose(tu.tournamentId.tournamentTypeId)) ~
                        ("id" -> tu.tournamentId.id) ~
                        ("game" -> Extraction.decompose(tu.tournamentId.gameId)*/
    /*            ("name" -> tu.tournament.game.name) ~
                    ("id" -> tu.tournament.game.id) ) )*/
}
    ))

class GuildSerializer extends CustomSerializer[Guild](formats => ( {
    PartialFunction.empty
}, {
    case t: Guild => implicit val formats: Formats = DefaultFormats + new EntitySerializer
        Extraction.decompose(t) merge
            render(("members" -> Extraction.decompose(t.members)) ~
                ("tournaments" -> Extraction.decompose(t.getTournaments()))) removeField {
            //Don't need to include guild info since it'd be recursive
            case ("guild", _) => true
            //Don't reveal the users email!
            case ("email", _) => true
            //case ("guildId", _) => true
            //case ("userId", _) => true
            case ("details", _) => true
            //case("gameId", _) => true
            case _ => false
        }
}))

//[T](mt: Manifest[T](implicit val bindingModule: BindingModule) https://github.com/dickwall/subcut/blob/master/GettingStarted.md#creating-an-injectable-class ?
class EntitySerializer[T: Manifest] extends CustomSerializer[Class[T]](formats => ( {
    PartialFunction.empty
}, {
    case eu: EventUser =>
        implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer //+ new EntitySerializer
        Extraction.decompose(eu) merge
            render(
                ("event" -> Extraction.decompose(eu.event)) ~
                ("user" -> Extraction.decompose(eu.user))) removeField {
            case ("customerId", _) => true
            case ("recieptId", _) => true
            case ("admins", _) => true
            case("moderators", _) => true
            case("payments", _) => true
            case("games", _) => true
            case("email", _) => true
            case _ => false
        }
    case u: User =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer + new GuildSerializer
        Extraction.decompose(u) removeField {
            case ("User", _) => true
            case ("email", _) => true
            case _ => false
        } /*merge render(
            //TODO Get rid of nasty coupling to repository implementation. How to mix in Injectable?
            ("events" -> Extraction.decompose(u.getAssociatedEvents(new EventUserRepository))) ~
                ("tournaments" -> {
                    val tRepo = new TeamRepository
                    /*Extraction.decompose(u.getAssociatedTournaments(new TournamentUserRepository))*/
                    val utour = u.getAssociatedTournaments(new TournamentUserRepository, new TeamUserRepository, new TournamentRepository)

                    utour.map(x =>
                        ("id" -> x.id) ~
                            ("name" -> x.details.flatMap(u => u.name)) ~
                            ("game" -> x.gameId.name) ~
                            ("tournamentType" -> x.tournamentTypeId.name) ~
                            ("eventId" -> x.eventId.id) ~
                            ("teamPlay" -> x.tournamentTypeId.teamPlay) ~
                            ("users" -> x.users.size) ~
                            ("teams" -> x.teams.size))
                })
        )*/
    /*    case t: Guild =>
            implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer + new EntityDetailsSerializer ++ org.json4s.ext.JodaTimeSerializers.all + new EntityAuxillarySerializer
            Extraction.decompose(t) merge
                render(("members" -> Extraction.decompose(t.members)) ~
                    ("tournaments" -> Extraction.decompose(t.getTournaments()))) removeField {
                case ("guild", _) => true
                case ("email", _) => true
                case ("guildId", _) => true
                case ("userId", _) => true
                case _ => false
            } *//* merge
                render(("captain" -> t.getCaptain.globalHandle) ~
                    ("tournaments" ->
                        t.getTournaments(new TeamRepository).map(x =>
                            ("id" -> x.id) ~
                                ("name" -> x.details.flatMap(u => u.name)) ~
                                ("game" -> x.gameId.name) ~
                                ("tournamentType" -> x.tournamentTypeId.name) ~
                                ("eventId" -> x.eventId.id) ~
                                ("teams" -> x.teams.size)))) removeField {
                case ("Team", _) => true
                case _ => false
            }*/
    /* merge
      render("tournaments" -> Extraction.decompose(teamRepo.getByTeam(t))) merge
      render("events" -> Extraction.decompose(teamRepo.getByTeam(t).map(x => x.tournament.event).distinct.map(u => ("name" -> u.name) ~ ("id" -> u.id))))*/
    case g: Game =>
        import com.esports.gtplatform.dao.Squreyl._
        implicit val formats: Formats = DefaultFormats
        Extraction.decompose(g) merge
            render("tournamentTypes" -> inTransaction {
                Extraction.decompose(g.tournamentTypes)
            })
    case e: Event =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer + new EntityAuxillarySerializer
        Extraction.decompose(e) merge
            render(
                ("admins" -> e.getAdmins.map(x => ("Name" -> x.globalHandle) ~ ("id" -> x.id))) ~
                    ("moderators" -> e.getModerators.map(x => ("Name" -> x.globalHandle) ~ ("id" -> x.id))) ~
                    ("teams" -> e.tournaments.foldLeft(0)((b, a) => b + a.teams.size)) ~
                    ("users" -> e.users.size) ~
                    ("tournaments" -> e.tournaments.size) ~
                    ("payments" -> Extraction.decompose(e.payments.filter(x => x.isEnabled))) ~
                    ("games" -> e.tournaments.map(x => x.game).groupBy(m => m)
                        .map(y => ("name" -> y._1.name) ~
                        ("id" -> y._1.id) ~
                        ("gameType" -> Extraction.decompose(y._1.gameType)) ~
                        ("filename" -> y._1.logoFilename) ~
                        ("count" -> y._2.size)
                        )))
    case t: Tournament =>
        implicit val formats: Formats = DefaultFormats + new LinkObjectEntitySerializer ++ org.json4s.ext.JodaTimeSerializers.all + new EntityDetailsSerializer + new EntityAuxillarySerializer
        var tour = Extraction.decompose(t) merge
            render(("users" -> t.users.size) ~
                ("teams" -> t.teams.size) ~
                ("tournamentType" -> Extraction.decompose(t.tournamentType)) ~
                ("game" -> Extraction.decompose(t.game)) ~
                ("details" -> Extraction.decompose(t.details)))
        if(t.details.isDefined)
        {
            if(t.details.get.prizes.isDefined)
                tour = tour.replace(List("prizes"),parse(t.details.get.prizes.get))
        }
        tour
}
    ))

object GTSerializers {
    val mapperSerializers = List(new LinkObjectEntitySerializer, new EntityDetailsSerializer, new com.esports.gtplatform.json.DateSerializer, new EntityAuxillarySerializer, new GuildSerializer, new EntitySerializer)
}
