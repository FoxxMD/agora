package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.Injectable
import com.esports.gtplatform.business._
import com.fasterxml.jackson.core.JsonParseException
import com.googlecode.mapperdao.Persisted
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json._
import org.slf4j.LoggerFactory
import models._

/**
 * Created by Matthew on 7/24/2014.
 */

/*Traits are the shit. They are similar to interfaces except they can have properties and defined methods.
* Use traits to build characteristics for a class or object. They can be mixed in using "extend" and then "with" for multiple mixins.
*
* In this file we are building the characteristics that we will make up a controller. */

trait BasicServletWithLogging extends ScalatraServlet {

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }

  val logger = LoggerFactory.getLogger(getClass)
  val pageSize = 300
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
    case t: Throwable =>
      logger.error(t.getMessage, t)
      halt(500, "Something went wrong!")
  }
}

trait RESTController extends BasicServletWithLogging with JacksonJsonSupport with MethodOverride with Injectable {
  /*ScalatraServlet = base for HTTP interaction in Scalatra
  * JacksonJsonSupport = Provide support for converting responses into JSON and conversion between JSON
  * MethodOverride = Add support for non-standard PUT/PATCH verbs
  * Injectable = Support for injecting dependencies using subcut */

  //Providing conversion between primitives and JSON, with added support for serializing the GameType enumeration.
  //Eventually will have to add support for all Enumeration types used.
  protected implicit val jsonFormats: Formats = DefaultFormats ++ GTSerializers.mapperSerializers ++ org.json4s.ext.JodaTimeSerializers.all + new org.json4s.ext.EnumNameSerializer(GameType)
  before() {

    //Lets the controller know to format the response in json so we don't have to specify on each action.
    contentType = formats("json")
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

trait APIController extends RESTController with StandardController {
  override def doAuthCheck() = {
    authApi()
  }
}

trait TeamControllerT extends StandardController {
  idType = "Team"
  val teamRepo = inject[TeamRepo]
  var requestTeam: Option[Team with Persisted] = None
  before("/:id/?*") {
    val p = params.getOrElse("id", halt(400, idType + " Id parameter is missing"))
    val i = toInt(p).getOrElse(halt(400, idType + " Id was not a valid integer"))
    paramId = Some(i)
  }
  before("/:id/?*") {
    teamRepo.get(paramId.get) match {
      case Some(t: Team with Persisted) =>
        requestTeam = Some(t)
      case None => halt(400, "No team exists with the Id " + paramId.get)
    }
  }
}
trait GameControllerT extends StandardController {
  idType = "Game"
  val gameRepo = inject[GameRepo]
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
  val eventRepo = inject[EventRepo]
  var requestEvent: Option[Event with Persisted] = None
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
}