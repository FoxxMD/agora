package com.esports.gtplatform.controllers

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.googlecode.mapperdao.Query._
import dao.Daos._
import dao.{UserEntity, UserIdentityEntity}
import models._
import org.scalatra.auth.{ScentryConfig, ScentryStrategy, ScentrySupport}
import org.scalatra.{ScalatraBase, Unauthorized}
import org.slf4j.LoggerFactory

/**
 * Created by Matthew on 7/28/2014.
 */
trait AuthenticationSupport extends ScentrySupport[User] {
  self: ScalatraBase =>

  protected def fromSession = { case id: String =>  queryDao.querySingleResult(select from UserEntity where UserEntity.id === id.toInt).get }
  protected def toSession   = { case usr: User => usr.id.toString }

  val realm = "Token Authentication"
  protected val scentryConfig = new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  /*override protected def configureScentry() = {
    scentry.unauthenticated {
      scentry.strategies("Bearer").unauthenticated()
    }
  }*/

  override protected def registerAuthStrategies() = {
    scentry.register("UserPassword", app => new UserPasswordStrategy(app))
    scentry.register("Token", app => new TokenStrategy(app))
    scentry.register("TokenOpt", app => new TokenOptStrategy(app))
  }

  // verifies if the request is a Bearer request
  protected def authToken()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    val baReq = new TokenAuthRequest(request)
    if(!baReq.providesAuth) {
      halt(401, "Unauthenticated")
    }
    /*    if(!baReq.isTokenAuth) {
          halt(400, "Bad Request")
        }*/
    scentry.authenticate("Token")
  }

  protected def authOptToken()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("TokenOpt")
  }
  protected def authUserPass()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("UserPassword")
  }

}

class TokenStrategy (protected override val app: ScalatraBase) extends ScentryStrategy[User]{

  implicit def request2TokenAuthRequest(r: HttpServletRequest) = new TokenAuthRequest(r)

  protected def getUserId(user: User): Int = user.id

  override def isValid(implicit request: HttpServletRequest) = request.providesAuth//request.isBearerAuth && request.providesAuth

  // catches the case that we got none user
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    app halt Unauthorized()
  }

  // overwrite required authentication request
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = validate(request.token)

  protected def validate(token: String): Option[User] = {
  queryDao.lowLevelQuery(UserEntity,
      """
        |select u.*
        |from users u
        |inner join tokens t on t.id=u.id
        |where t.token=?
      """.stripMargin,List(token)).headOption
  }
}

class TokenOptStrategy (protected override val app: ScalatraBase) extends TokenStrategy(app) with ScentryStrategy[User] {
  override def isValid(implicit request: HttpServletRequest) = true
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {

  }
}

class TokenAuthRequest(r: HttpServletRequest) {

  private val AUTHORIZATION_KEYS = List("Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")
  def parts = authorizationKey map { r.getHeader(_).split(" ", 2).toList } getOrElse Nil
  //def scheme: Option[String] = parts.headOption.map(sch => sch.toLowerCase(Locale.ENGLISH))
  def token : String = parts.lastOption getOrElse ""

  private def authorizationKey = AUTHORIZATION_KEYS.find(r.getHeader(_) != null)

  //def isTokenAuth = (false /: scheme) { (_, sch) => sch == "bearer" }
  def providesAuth = authorizationKey.isDefined

}

trait LoginSupport extends ScentrySupport[User] {
  self: ScalatraBase =>

  protected def fromSession = { case id: String =>  queryDao.querySingleResult(select from UserEntity where UserEntity.id === id.toInt).get }
  protected def toSession   = { case usr: User => usr.id.toString }

  val realm = "Login Authentication"
  protected val scentryConfig = new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  override protected def registerAuthStrategies() = {
    scentry.register("UserPassword", app => new UserPasswordStrategy(app))
  }

  // verifies if the request is a Bearer request
  protected def auth()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("UserPassword")
  }
}


class UserPasswordStrategy(protected val app: ScalatraBase)(implicit request: HttpServletRequest, response: HttpServletResponse)
  extends ScentryStrategy[User] {

  val logger = LoggerFactory.getLogger("UserPasswordStrategy")

  override def name: String = "UserPassword"

  private def login = app.params.getOrElse("email", "")
  private def password = app.params.getOrElse("password", "")


  /***
    * Determine whether the strategy should be run for the current request.
    */
  override def isValid(implicit request: HttpServletRequest) = {
    login != "" && password != ""
  }

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    val uie = UserIdentityEntity
    logger.info("attempting authentication")
    val maybeUser = queryDao.querySingleResult(select from uie where uie.providerId === "userpass" and uie.userId === login and uie.password === password)
    maybeUser match {
      case Some(ident: UserIdentity) =>
        logger.info("authentication succeeded for " + ident.email.get)
        val possibleToken = jdbc.queryForMap("""
                           |select t.*
                           |from tokens t
                           |where t.id=?
                         """.stripMargin,ident.user.id).map {m => m.string("token")}
        possibleToken match {
          case Some(token: String) =>
            //TODO token issueDate refresh
            response.addHeader("Authorization", token)
          case None =>
            val newToken = java.util.UUID.randomUUID.toString
            jdbc.update(
              """
                |INSERT INTO tokens VALUES(?,?,NULL)
              """.stripMargin,List(ident.user.id,newToken))
            //TODO ensure this udpate occurs before sending token
            response.addHeader("Authorization", newToken)
        }
        Option(ident.user)
      case None =>
        logger.info("authentication failed")
        None
    }
  }
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    //app.redirect("/login")
    app halt Unauthorized()
  }

}
