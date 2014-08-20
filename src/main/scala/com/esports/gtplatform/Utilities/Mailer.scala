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
}
