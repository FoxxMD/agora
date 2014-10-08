package com.esports.gtplatform.Utilities


import java.util.concurrent.TimeoutException

import com.roundeights.mailgun.{Email, MailSender, Mailgun}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Created by Matthew on 8/20/2014.
 */
class Mailer(eventName: String = "Gamefest") {
  val logger = LoggerFactory.getLogger(getClass)
  val sender: MailSender =  new Mailgun("sandboxebc2e9178fa5401b9169d5c545a89379.mailgun.org", "key-77e9200b3bc681ca40e0c58267b13941")
  val defaultFrom = "postmaster@sandboxebc2e9178fa5401b9169d5c545a89379.mailgun.org"

  private def sendMail(mail: Email) = {
    val response: Future[MailSender.Response] = sender.send(mail)

    try {
      Await.result(
        response.map(data => {
          println( data.id )
          println( data.message )
          logger.info("Email successfully sent.")
        }),
        5.seconds
      )
    }
    catch {
      case (t: TimeoutException) =>
        logger.info("Email took longer than 5 seconds to send. Network or mailgun problem?")
    }
    finally {
      sender.close
    }
  }

def sendConfirm(toAddress: String, handle: String, token: String) = {

  val confirmEmail =
    Email(
  to = Email.Addr(toAddress, handle),
  from = Email.Addr(defaultFrom, eventName + " Staff"),
  subject = s"Confirm Your Account for $eventName",
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
  logger.info("Attempting to send confirmation email to " + toAddress)
  sendMail(confirmEmail)
}
    def sendForgotPassword(toAddress: String, token: String, alreadyTried: Boolean = false) = {

        val forgottenEmail =
            Email(
                to = Email.Addr(toAddress),
                from = Email.Addr(defaultFrom, eventName + " Staff"),
                subject = s"Forgotten Password Recovery for $eventName",
                body = Email.text(s"""Hello,
        |
        | Someone has requested that the password associated with this account be reset.
        |
        | To reset your password please follow this link: http://gtgamefest.com/passwordReset?token=$token
        |
        | If you did not request to have your password reset or feel this was an error please contact us at feedback@gtgamefest.com
        |
        | Thanks!
        |
        | -$eventName Staff
      """.stripMargin)
            )
        logger.info("Attempting to send forgotten password email to " + toAddress)
        sendMail(forgottenEmail)
    }

  def sendAlreadyRegistered(toAddress: String) = {

    val mail =
      Email(
        to = Email.Addr(toAddress),
        from = Email.Addr(defaultFrom, eventName + " Staff"),
        subject = s"Registered Account for $eventName",
        body = Email.text(s"""Hello,
        |
        | Someone tried to use your email address to register for $eventName, but this address is already associated
        | to an account! If you have forgotten your password please visit http://gtgamefest.com/resetPassword to start
        | the reset process. If you feel this email is an error please contact our staff.
        |
        | Thanks!
        |
        | -$eventName Staff
      """.stripMargin)
      )
    logger.info("Attempting to send already registered email to " + toAddress)
    sendMail(mail)

  }

}
