package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
class User(val email: String, val createdDate: DateTime, val role: String, val details: UserDetails, val teams: Set[TeamUser], val events: Set[EventUser], val tournaments: Set[TournamentUser]) {

}

class UserDetails(var user: User, val firstName: Option[String], val lastName: Option[String], val globalHandle: String, val password: String, val salt: String)
{

}
