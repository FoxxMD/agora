package com.esports.gtplatform.models

/**
 * Created by Matthew on 11/6/2014.
 */
case class ConfirmationToken(userIdentId: Int, token: String, eventId: Option[Int])
case class ApiKey(id: Option[Int], token: String) extends DomainEntity[ApiKey]
case class PasswordToken(id: Int, token: String)
case class WebToken(id: Option[Int], token: String) extends DomainEntity[WebToken]
