package com.esports.gtplatform.models

/**
 * Created by Matthew on 11/6/2014.
 */
case class ConfirmationToken(userIdentId: Int, token: String, eventId: Option[Int]) {
    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(userIdentId = 0, token = "", eventId = Some(0))
}
case class ApiKey(id: Option[Int], token: String) extends DomainEntity[ApiKey] {
    def this() = this(id = Some(0), token = "")
}
case class PasswordToken(id: Int, token: String)
case class WebToken(id: Option[Int], token: String) extends DomainEntity[WebToken] {
    def this() =this(id = Some(0), token = "")
}
