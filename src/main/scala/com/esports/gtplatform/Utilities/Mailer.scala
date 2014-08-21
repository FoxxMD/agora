package com.esports.gtplatform.Utilities

import skinny.mailer._

/**
 * Created by Matthew on 8/20/2014.
 */
class Mailer(eventName: String = "Gamefest") {

  class MyMailer extends SkinnyMailer

  def sendConfirm(toAddress: String, handle: String, token: String) = {
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
