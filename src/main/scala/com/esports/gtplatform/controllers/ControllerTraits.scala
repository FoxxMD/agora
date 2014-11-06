package com.esports.gtplatform.controllers

import com.esports.gtplatform.business._
import com.esports.gtplatform.business.services.{RosterServiceT, TeamServiceT}
import com.esports.gtplatform.models.Team
import com.fasterxml.jackson.core.JsonParseException
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.exceptions.QueryException
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json._
import org.slf4j.LoggerFactory
import models._
import org.springframework.jdbc.BadSqlGrammarException
import scaldi.Injectable

import scala.xml.Node

/**
 * Created by Matthew on 7/24/2014.
 */

/*Traits are the shit. They are similar to interfaces except they can have properties and defined methods.
* Use traits to build characteristics for a class or object. They can be mixed in using "extend" and then "with" for multiple mixins.
*
* In this file we are building the characteristics that we will make up a controller. */

trait BasicServletWithLogging extends ScalatraFilter {

    def toInt(s: String): Option[Int] = {
        try {
            Some(s.toInt)
        } catch {
            case e: Exception => None
        }
    }

    val logger = LoggerFactory.getLogger(getClass)
    val pageSize = 70
    error {
        case p: JsonParseException =>
            logger.error(p.getMessage, p)
            halt(400, "Request data was malformed. Make sure JSON is formatted properly.")
        case m: org.json4s.MappingException =>
            logger.error(m.getMessage, m)
            halt(400, "Request was badly formed, are you missing a parameter?")
        case n: NotImplementedError =>
            logger.error(n.getMessage, n)
            halt(500, "We forgot to implement something...")
        case q: QueryException =>
            logger.error(q.getMessage, q)
            halt(500, "Database problems...")
        case my: BadSqlGrammarException =>
            logger.error(my.getMessage, my)
            halt(500, "Database problems...")
        case np: NullPointerException =>
            logger.error(np.getMessage, np)
            halt(500, "Something went wrong!")
        case e: Error =>
            logger.error(e.getMessage, e)
            halt(500, "Something went wrong!")
        case t: Throwable =>
            logger.error(t.getMessage, t)
            halt(500, "Something went wrong!")
    }
}

trait RESTController extends BasicServletWithLogging with JacksonJsonSupport with MethodOverride {
    /*ScalatraServlet = base for HTTP interaction in Scalatra
    * JacksonJsonSupport = Provide support for converting responses into JSON and conversion between JSON
    * MethodOverride = Add support for non-standard PUT/PATCH verbs */

    //Providing conversion between primitives and JSON, with added support for serializing the GameType enumeration.
    //Eventually will have to add support for all Enumeration types used.
    protected implicit val jsonFormats: Formats = DefaultFormats ++ GTSerializers.mapperSerializers + new EntityDetailsSerializer + new EntitySerializer
    before() {

        //Lets the controller know to format the response in json so we don't have to specify on each action.
        contentType = formats("json")
    }
    /*
        Setting the content type to json for every response is great for automatically parsing object to json
        but it's not useful if you want to return other non json responses and don't want to manually set it everytime.

        EX. Using an ActionResult like Ok() and wanting to be able to do Ok(someUser) and Ok("some text response)

        The below function checks response bodies to see if they are strings and then sets the contenttype so the client
        knows how to parse the response.

     */
    override def renderResponse(actionResult: Any) {
        actionResult match {
            case a:ActionResult =>
                a.body match {
                    case s:String =>
                        logger.debug("[Response Body] It's a string")
                        if(parseOpt(s).isEmpty)
                        {
                            logger.debug("[Response Body] It's not json, switching format to plain/text")
                            contentType = formats("txt")
                        }
                        else{
                            logger.debug("[Response Body] It's json.")
                        }

                    case _ =>
                    logger.debug("[Response Body] It's not a string")
                }
            case s: String =>
                logger.debug("[Response Body] Result was a string")
                contentType = formats("txt")
            case _ =>
            logger.debug("[Response Body] no hit")
        }
            renderResponseBody(actionResult)
    }
}

/*Add support for Authentication and CORS.

 * Authentication can be accessed by using one of the authentication methods in AuthStrategy -- authToken, authOptToken, or authUserPass
 *
 * Eventually there will be an API key stategy as well
 *
*/
trait StandardController extends RESTController with AuthenticationSupport with CorsSupport {
    var authUser: Option[User] = None

    def doAuthCheck(): Unit = None

    var idType: String = ""
    var paramId: Option[Int] = None
    var userParamId: Option[Int] = None

    before() {
        if (request.getRemoteHost != "127.0.0.1") {
            //If not from origin then halt immediately.
            logger.info("Non-Origin request from " + request.getRemoteHost)
            doAuthCheck()
        }
    }
}

//Add mandatory authentication on every action
trait StandardWithAuth extends StandardController {
    before() {
        auth()
    }
}

//Provide support for user-aware auth on every action
trait StandardWithOptAuth extends StandardController {
    before() {
        authOpt()
    }
}

trait APIController extends StandardController {
    override def doAuthCheck() = {
        authApi()
    }
}

trait GuildControllerT extends StandardController {
    idType = "Guild"
    def guildRepo: GuildRepo
    def guildUserRepo: GuildUserRepo
    def userRepo: UserRepo
    var requestGuild: Guild with Persisted = null
    before("/:id/?*") {
/*        requestGuild = params.get("id").fold {
            halt(400, idType + " Id parameter is missing")
        }(toInt)
            .fold {
            halt(400, idType + " Id was not a valid integer")
        } {
            guildRepo.get(_).getOrElse(halt(400, "No team exists with the Id " + _))
        }*/
/*            val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
            val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
            paramId = Some(i)*/
    }
      before("/:id/?*") {
          val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
          val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
          paramId = Some(i)
        guildRepo.get(paramId.get) match {
          case Some(t: Guild with Persisted) =>
            requestGuild = t
          case None => halt(400, "No team exists with the Id " + paramId.get)
        }
      }
}

trait GameControllerT extends StandardController {
    idType = "Game"
    def gameRepo: GameRepo
    def gameTTLinkRepo: GameTTLinkRepo
    var requestGame: Option[Game with Persisted] = None
    before("/:id/?*") {
        val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
        val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
        paramId = Some(i)
    }
    before("/:id/?*") {
        gameRepo.get(paramId.get) match {
            case Some(t: Game with Persisted) =>
                requestGame = Some(t)
            case None => halt(400, "No game exists with the Id " + paramId.get)
        }
    }
}

trait EventControllerT extends StandardController {
    idType = "Event"
    def eventRepo: EventRepo
    def eventUserRepo: EventUserRepo
    def tournamentRepo: TournamentRepo
    def userRepo: UserRepo
    def ttRepo: TournamentTypeRepo

    var requestEvent: Option[Event] = None
    var requestEventUser: Option[EventUser] = None
    var tournamentParamId: Option[Int] = None
    var requestTournament: Option[Tournament] = None
    var teamParamId: Option[Int] = None
    var requestTeam: Option[Team] = None

    before("/:id/?*") {
        val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
        val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
        paramId = Some(i)
    }
    before("/:id/?*") {
        eventRepo.get(paramId.get) match {
            case Some(t: Event with Persisted) =>
                requestEvent = Some(t)
            case None => halt(400, "No event exists with the Id " + paramId.get)
        }
    }
    before("/:id/users/:userId/?*") {
        val p = params.getOrElse("userId", halt(400, idType + " User Id parameter is missing"))
        val i = toInt(p).getOrElse(halt(400, idType + " User Id was not a valid integer"))
        userParamId = Some(i)
    }
    before("/:id/users/:userId/?*") {
        if (userParamId.isDefined) {
            requestEvent.get.users.find(x => x.userId == userParamId.get) match {
                case Some(eu: EventUser with Persisted) =>
                    requestEventUser = Some(eu)
                case None =>
                    logger.warn("Tried to modify an EventUser for a non-existent user " + userParamId.get + " on Event " + requestEvent.get.id)
                    halt(400, "This user is not in this event.")
                case _ =>
                    logger.warn("Did not find a match for event user")
            }
        }
        else {
            halt(400, "No user id paramter defined.")
        }
    }
    before("/:id/tournaments/:tourId/?*") {
        val ti = params.getOrElse("tourId", halt(400, "Tournament Id parameter is missing"))
        val tii = toInt(ti).getOrElse(halt(400, "Tournament Id was not a valid integer"))
        tournamentParamId = Some(tii)
    }
    before("/:id/tournaments/:tourId/?*") {
        if (tournamentParamId.isDefined) {
            tournamentRepo.get(tournamentParamId.get) match {
                case Some(t: Tournament with Persisted) =>
                    requestTournament = Some(t)
                case None => halt(400, "No tournament exists with the Id " + tournamentParamId.get + " exists.")
            }
        }
        else {
            halt(400, "No tournament id paramter defined.")
        }
    }
    before("\"/:id/tournaments/:tourId/teams/") {
        if(!requestTournament.get.tournamentType.teamPlay)
            halt(400,"This tournament is using a User Only play type. Change the game and play type to allow users.")
    }
    before("\"/:id/tournaments/:tourId/players/") {
        if(requestTournament.get.tournamentType.teamPlay)
            halt(400,"This tournament is using a Team Only play type. Change the game and play type to allow teams.")
    }
    before("/:id/tournaments/:tourId/teams/:teamId/?*"){
        val tdefined = params.getOrElse("teamId", halt(400,"Team id is missing"))
        val tInt = toInt(tdefined).getOrElse(halt(400,"Team Id was not a valid integer"))
        teamParamId = Some(tInt)
    }
    before("/:id/tournaments/:tourId/teams/:teamId/?*"){
        if(teamParamId.isDefined) {
            requestTournament.get.teams.find(x => x.id.get == teamParamId.get) match {
                case Some(t: Team with Persisted) =>
                requestTeam = Some(t)
                case None => halt(400, "No team with the Id " + teamParamId.get + " exists.")
                case _ => logger.warn("We missed the match!")
            }
        }
        else {
            halt(400, "No team id paramter defined.")
        }
    }
}

trait UserControllerT extends StandardController {
    idType = "User"
    def userRepo: UserRepo
    def userIdentRepo: UserIdentityRepo
    def requestUser: User = possibleUser.get
    def possibleUser: Option[User] = None
    before("/:id/?*") {
        val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
        if (p == "me")
            paramId = None
        else {
            val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
            paramId = Some(i)
        }
    }
    before("/:id/?*") {
        if (paramId.isDefined)
            userRepo.get(paramId.get) match {
                case Some(t: User) =>
                    val possibleUser = Some(t)
                case None =>
                    halt(400, "No user exists with the Id " + paramId.get)
            }
    }
}

trait TournamentT extends StandardController {
    idType = "Tournament"
    def tournamentRepo: TournamentRepo
    def tournamentUserRepo: TournamentUserRepo
    def tournamentDetailsRepo: TournamentDetailsRepo
    def possibleTournament: Option[Tournament] = None
    def requestTournament: Tournament = possibleTournament.get
    var requestTournamentUser: Option[TournamentUser] = None

    before("/:id/?*") {
        val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
            val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
            paramId = Some(i)

    }
    before("/:id/?*") {
        if (paramId.isDefined)
            tournamentRepo.get(paramId.get) match {
                case Some(t: Tournament) =>
                    val possibleTournament = Some(t)
                case None =>
                    halt(400, "No "+idType+" exists with the Id " + paramId.get)
            }
    }
    before("/:id/players/:tuId/?*"){
        val p = params.getOrElse("tuId", halt(400, "Tournament User Id parameter is missing"))
        val i = toInt(p).getOrElse(halt(400, "Tournament User Id was not a valid integer"))
        tournamentUserRepo.get(i) match {
            case Some(tu: TournamentUser) =>
                requestTournamentUser = Some(tu)
            case None =>
                halt(400, "No Tournament User with that Id exists.")
        }

    }

}

trait TeamT extends StandardController {
    idType = "Team"
    def teamRepo: TeamRepo
    def teamUserRepo: TeamUserRepo
    def teamService: TeamServiceT
    def rosterService: RosterServiceT
    def possibleTeam: Option[Team] = None
    def requestTeam: Team = possibleTeam.get

    before("/events/:eventId/tournaments/:tourId/teams/:id/?*") {
        val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
        if (p == "me")
            paramId = None
        else {
            val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
            paramId = Some(i)
        }
    }
    before("/events/:eventId/tournaments/:tourId/teams/:id/?*") {
        if (paramId.isDefined)
            teamRepo.get(paramId.get) match {
                case Some(t: Team) =>
                    val possibleTeam = Some(t)
                case None =>
                    halt(400, "No "+idType+" exists with the Id " + paramId.get)
            }
    }
}
