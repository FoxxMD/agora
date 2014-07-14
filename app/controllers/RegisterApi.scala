package controllers

import play.api.libs.json.Json
import play.api.mvc._
import securesocial.controllers.MailTokenBasedOperations
import securesocial.core._
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.services.SaveMode

import scala.concurrent.Future

class MyRegisterApi(override implicit val env: RuntimeEnvironment[BasicProfile]) extends BaseRegistrationApi[BasicProfile]

case class regInfo(token: String, firstName: Option[String], lastName: Option[String], password: String, globalHandle: Option[String])

trait BaseRegistrationApi[U] extends MailTokenBasedOperations[U] {

  def register(email: String, password: String) = Action.async { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    env.userService.findByEmailAndProvider(email, UsernamePasswordProvider.UsernamePassword).flatMap {
      case Some(user) =>
        env.mailer.sendAlreadyRegisteredEmail(user)
        Future.successful(Ok(Json.toJson(Map("error" -> "alreadysigned"))).as("application/json"))
      case None =>
        createToken(email, isSignUp = true).map { token =>
          env.mailer.sendSignUpEmail(email, token.uuid)
        }
        Future.successful(Ok(""))
    }
  }
  def handleRegister() = Action.async(parse.json) { implicit request =>
    implicit val regread = Json.reads[regInfo]
    val regResult = Json.fromJson(request.body).asOpt
    import scala.concurrent.ExecutionContext.Implicits.global
    regResult match {
      case Some(info) =>
        executeForToken(info.token, true, {
          t =>
            val newUser = BasicProfile(
            "userpass",
            t.email,
            info.firstName,
            info.lastName,
            Some("%s %s".format(info.firstName, info.lastName)),
            Some(t.email),
            None,
            AuthenticationMethod.UserPassword,
            passwordInfo = Some(env.currentHasher.hash(info.password))
            )

            val result = for (
              saved <- env.userService.save(newUser, SaveMode.SignUp) ;
              deleted <- env.userService.deleteToken(t.uuid)
            ) yield {
              if (UsernamePasswordProvider.sendWelcomeEmail)
                env.mailer.sendWelcomeEmail(newUser)
              val eventSession = Events.fire(new SignUpEvent(saved)).getOrElse(request.session)
              Future.successful(Ok(""))
            }
            result.flatMap(f => f)
        })
      case None => Future.successful(BadRequest(Json.toJson(Map("error" -> "Malformed JSON"))))
    }
  }
}