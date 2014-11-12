package com.esports.gtplatform.controllers

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.esports.gtplatform.Utilities.{ApiSecurity, PasswordSecurity}
import com.esports.gtplatform.business.{UserIdentityRepo, UserRepo, WebTokenRepo}
import com.esports.gtplatform.models.WebToken
import models._
import org.scalatra.auth.{ScentryConfig, ScentryStrategy, ScentrySupport}
import org.scalatra.{ScalatraBase, Unauthorized}
import org.slf4j.LoggerFactory


/**
 * Created by Matthew on 7/28/2014.
 */

/* This is a bit of a mess, I will be changing things soon.
*
* A brief fly-over:
*
* Scalatra let's us implement our own authentication using two Traits -- ScentrySupport and ScentryStrategy
*
* ScentrySupport determines which strategy for authentication is used as well as storing/retrieving from session
* ScentryStrategy implements the actual authentication. It takes an input and returns a User based on some method (or not if invalid!)
*
*
* TokenStrategy gets a string from the request headers under the key "Authorization" and checks it against the token table in the DB.
* If it finds a token it returns the corresponding User to the controller using a low-level join on the token Id column. This is what will be used for the majority of
* auth in the application. The client app will set the token in the header after recieving it upon login and then send it with every request.
*
* TokenOptStrategy does the same thing but does not send a 401 response if not authenticated. This is helpful if we still want to identify the user(if there is one) to provide
* extra information(such as for an admin, or less info if they are anonymous)
*
* UserPasswordStrategy gets "email" and "password" parameters from the request and checks them against UserIdentities in the DB.
* If it finds one it first checks the token table for an existing token, if it doesn't find one it creates a new token and adds an entry in the tokens DB. It then
* adds the token to the response headers and returns the User to the controller. The client app recieves the response and saves the token from headers. Whicch then gets used with
* the TokenStrategy from above.
*
* To use authentication mixin one of the AuthController traits into your controller or use StandardController with these methods:
* auth() -- Mandatory authentication
* authOpt -- Optional authentication
* authUserPass -- Should only be used for login
*
* All three return a User object you can then use in your controller(if authentication succeeds)
*
* */
trait AuthenticationSupport extends ScentrySupport[User] {
  self: ScalatraBase =>
    import scaldi.Injectable._
    val userRepo = inject[UserRepo]

  protected def fromSession = {
    case id: String => userRepo.get(id.toInt).get
  }

  protected def toSession = {
    case usr: User => usr.id.toString
  }

  val realm = "Token Authentication"
  protected val scentryConfig = new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  override protected def configureScentry() = {
    scentry.unauthenticated {
      scentry.strategies("Token").unauthenticated()
    }
  }

  override protected def registerAuthStrategies() = {
    scentry.register("UserPassword", app => new UserPasswordStrategy(app))
    scentry.register("Token", app => new TokenStrategy(app))
    scentry.register("Api", app => new ApiStrategy(app))
    scentry.register("TokenOpt", app => new TokenOptStrategy(app))
  }

  // verifies if the request is a Bearer request
  protected def auth()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("Token", "Api")
    scentry.authenticate("Api")
    if (!scentry.isAuthenticated)
      halt(401,"Unauthorized request.")

  }

  protected def authOpt()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("TokenOpt")
  }

  protected def authUserPass()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("UserPassword", "Token")
    if (!scentry.isAuthenticated)
      scentry.strategies("UserPassword").unauthenticated()
  }

  protected def authApi()(implicit request: HttpServletRequest, response: HttpServletResponse) = {
    scentry.authenticate("Api")
    if (!scentry.isAuthenticated)
      scentry.strategies("Api").unauthenticated()
  }
}

class TokenStrategy(protected override val app: ScalatraBase) extends ScentryStrategy[User] {
    import scaldi.Injectable._
    val webTokenRepo = inject[WebTokenRepo]
    val userRepo = inject[UserRepo]
  val logger = LoggerFactory.getLogger("TokenStrategy")
  private val keys = List("Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")

  implicit def request2TokenAuthRequest(r: HttpServletRequest) = new TokenAuthRequest(r, keys)

  protected def getUserId(user: User): Int = user.id.get

  override def isValid(implicit request: HttpServletRequest) = request.providesAuth //request.isBearerAuth && request.providesAuth

  // catches the case that we got none user
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    app halt Unauthorized("Could not authorize request, either no token was provided or the token was invalid.")
  }

  // overwrite required authentication request
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = validate(request.token)

  protected def validate(token: String): Option[User] = {
     webTokenRepo.getByToken(token) match {
         case Some(w: WebToken) =>
             userRepo.get(w.id) match {
                 case Some(u: User) =>
                     val newToken = java.util.UUID.randomUUID.toString
                     webTokenRepo.update(w.copy(token = newToken))
                     Option(u)
                 case None =>
                     logger.error("[Authentication] Could not find user associated with token " + token)
                     None
             }
         case None =>
             logger.warn("[Authentication] Could not find token " + token)
             None
     }
  }
}

class TokenOptStrategy(protected override val app: ScalatraBase) extends TokenStrategy(app) with ScentryStrategy[User] {
  override def isValid(implicit request: HttpServletRequest) = true

  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {

  }
}

class ApiStrategy(protected override val app: ScalatraBase) extends ScentryStrategy[User] {
  private val keys = List("ApiKey", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")

  implicit def request2TokenAuthRequest(r: HttpServletRequest) = new TokenAuthRequest(r, keys)

  protected def getUserId(user: User): Int = user.id.get

  override def isValid(implicit request: HttpServletRequest) = request.providesAuth //request.isBearerAuth && request.providesAuth

  // catches the case that we got none user
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    app halt Unauthorized("Could not authorize request, either API key was not provided or the key was invalid.")
  }

  // overwrite required authentication request
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = validate(request, request.token)

  protected def validate(implicit request: HttpServletRequest, token: String): Option[User] = {
    //TODO move secret to config file and change applicationName to something more dynamic to increase security strength
    if (ApiSecurity.checkHMAC("gamefestSecret", "gamefest", request.getRemoteHost, token)) {
      None//TODO implement
    }
    else
      None
  }
}

class TokenAuthRequest(r: HttpServletRequest, KeyList: List[String]) {

  private val AUTHORIZATION_KEYS = KeyList

  //List("Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")
  def parts = authorizationKey map {
    r.getHeader(_).split(" ", 2).toList
  } getOrElse Nil

  //def scheme: Option[String] = parts.headOption.map(sch => sch.toLowerCase(Locale.ENGLISH))
  def token: String = parts.lastOption getOrElse ""

  private def authorizationKey = AUTHORIZATION_KEYS.find(r.getHeader(_) != null)

  //def isTokenAuth = (false /: scheme) { (_, sch) => sch == "bearer" }
  def providesAuth = authorizationKey.isDefined

}

class UserPasswordStrategy(protected val app: ScalatraBase)(implicit request: HttpServletRequest, response: HttpServletResponse)
  extends ScentryStrategy[User] {

    import scaldi.Injectable._
    val webTokenRepo = inject[WebTokenRepo]
    val userIdentRepo = inject[UserIdentityRepo]
    val userRepo = inject[UserRepo]

  val logger = LoggerFactory.getLogger("UserPasswordStrategy")

  override def name: String = "UserPassword"

  private def login = app.params.getOrElse("email", "")

  private def password = app.params.getOrElse("password", "")


  /** *
    * Determine whether the strategy should be run for the current request.
    */
  override def isValid(implicit request: HttpServletRequest) = {
    login != "" && password != ""
  }

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {

    userIdentRepo.getByUserPass(login) match {
        //We found a user with this email!
      case Some(ident: UserIdentity) =>

        /* Hash the provided password and check against the stored hash of the valid password for this identity.
        *  We do this here because the checking method uses a technique to prevent against timing attacks and is
        *  inherently slower because of it. Putting it in the query would slow down query time which would increase blocking.
        * */
        if (!PasswordSecurity.validatePassword(password, ident.password.get)) {
            logger.warn("[Authentication] Password invalid for User " + ident.userId)
            None
        }
        else {
          /*logger.info("authentication succeeded for " + ident.user.id)*/
          //Check to see if this user has a token already
          webTokenRepo.get(ident.userId) match {
            case Some(webToken: WebToken) =>
              //They have a token, return it in the header
              //TODO token issueDate refresh
              response.addHeader("Authorization", webToken.token)
            case None =>
              //They don't have a token, create a new one and return it in the header
              val newToken = java.util.UUID.randomUUID.toString
              webTokenRepo.create(WebToken(ident.userId, newToken))
              //TODO ensure this udpate occurs before sending token
              response.addHeader("Authorization", newToken)
          }
          userRepo.get(ident.userId)
        }
      case None =>
        //Did not match an email
        logger.info("[Authentication] UserPass strategy failed, " + login + ", found in DB.")
        None
    }
  }

  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    app halt Unauthorized("Username or Password incorrect.")
  }

}
