package models

import com.esports.gtplatform.models.{DomainEntity, Team}

/**
 * Created by Matthew on 6/30/2014.
 */

/* Link objects exist to provide extra information about a relationship between two domain objects. Currently it's only
 * simple things like isPresent for Teams at a Tournament, or Users at an Event -- but I'm sure eventually the relationship will
  * grow more complex and require more information.
  *
  * These really should be case classes but ATM they are easier to handle as mutable datas.*/

case class TeamUser(teamId: Option[Int], userId: Int, isCaptain: Boolean = false, id: Option[Int] = None) {
    var team: Team = null
    var user: User = null

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(teamId = Some(0), userId = Some(0), isCaptain = false, id = Some(0))

}
case class GuildUser(guildId: Option[Int], userId: Int, isCaptain: Boolean = false, id: Option[Int] = None) {
   var guild: Guild = null
    var user: User = null

    def this() = this(guildId = Some(0), userId = 0, isCaptain = false, id = Some(0))
}

case class EventUser(eventId: Int, userId: Int, isPresent: Boolean = false, isAdmin: Boolean = false, isModerator: Boolean = false, hasPaid: Boolean = false, paymentType: Option[String] = None, receiptId: Option[String] = None, customerId: Option[String] = None, id: Option[Int] = None) {
    //For hydration
    var event: Option[Event] = None
    var user: Option[User] = None

    def this() = this(eventId = 0, userId = 0, isPresent = false, isAdmin = false, isModerator = false, hasPaid = false, paymentType = Some(""), receiptId = Some(""), customerId = Some(""), id = Some(0))

}

case class TournamentUser(userId: Int, tournamentId: Int, isPresent: Boolean = false, isAdmin: Boolean = false, isModerator: Boolean = false, id: Option[Int] = None) {
    var tournament: Tournament = null
    var user: User = null

    def this() = this(userId = 0, tournamentId = 0, isPresent = false, isAdmin = false, isModerator = false, id = Some(0))
}

case class TournamentType(name: String = "A Tourney Type", teamPlay: Boolean = true, id: Option[Int] = None) {
    def this() = this(name = "", teamPlay = true, id = Some(0))
}

case class GameTournamentType(gameId: Int = 0, tournamentTypeId: Int = 0, id: Option[Int] = None) extends DomainEntity[GameTournamentType] {
    def this() = this(gameId = 0, tournamentTypeId = 0, id = Some(0))
}
case class GuildGame(guildId: Option[Int] = None, gameId: Int, id: Option[Int] = None) {
    def this() = this(guildId = Some(0), gameId = 0, id = Some(0))
}
