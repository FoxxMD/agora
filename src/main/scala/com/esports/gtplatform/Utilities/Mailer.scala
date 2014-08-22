package com.esports.gtplatform.Utilities

import com.esports.gtplatform.Utilities.MailgunMine._
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import skinny.mailer._
/*import com.roundeights.mailgun.{Email, MailSender, Mailgun}*/

import scala.concurrent.Future

/**
 * Created by Matthew on 8/20/2014.
 */
class Mailer(eventName: String = "Gamefest") {
  val logger = LoggerFactory.getLogger(getClass)
  class MyMailer extends SkinnyMailer
  val sender: MailSender =  new Mailgun("sandboxebc2e9178fa5401b9169d5c545a89379.mailgun.org", "key-77e9200b3bc681ca40e0c58267b13941")

/*  def sendConfirm(toAddress: String, handle: String, token: String) = {
    val mail = new MyMailer

    mail.
    to(toAddress).
    subject("Confirm your registration for " + eventName).
    body(
      s"""Hello $handle!
        |
        | Please complete your registration and confirm this email address was used by you to register for $eventName
        | by following this link: http://gtgamefest.com/confirmRegistration?token=$token
        |
        | Thanks!
        |
        | -$eventName Staff
      """.stripMargin).deliver()
  }*/
def sendConfirm(toAddress: String, handle: String, token: String) = {

  val response: Future[MailSender.Response] = sender.send(
  to = Email.Addr(handle, toAddress),
  from = Email.Addr(eventName + " Staff", "postmaster@sandboxebc2e9178fa5401b9169d5c545a89379.mailgun.org"),
  subject = "a test email",
  body = Email.text(s"""Hello $handle!
        |
        | Please complete your registration and confirm this email address was used by you to register for $eventName
        | by following this link: http://gtgamefest.com/confirmRegistration?token=$token
        |
        | Thanks!
        |
        | -$eventName Staff
      """.stripMargin)
  )
  logger.info("Mail was sent? " + response.isCompleted)
  response.map(r => {
    logger.info(r.id)
    logger.info(r.message)
  })
}

  def sendAlreadyRegistered(toAddress: String) = {
    val mail = new MyMailer

    mail.
      to(toAddress).
      subject("Confirm your registration for " + eventName).
      body(
        s"""Hello,
        |
        | Someone tried to use your email address to register for $eventName, but this address is already associated
        | to an account! If you have forgotten your password please visit http://gtgamefest.com/resetPassword to start
        | the reset process. If you feel this email is an error please contact our staff.
        |
        | Thanks!
        |
        | -$eventName Staff
      """.stripMargin).deliver()
  }

}
