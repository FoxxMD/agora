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

}
case class GuildUser(guildId: Option[Int], userId: Int, isCaptain: Boolean = false, id: Option[Int] = None) {
   var guild: Guild = null
    var user: User = null
}

case class EventUser(eventId: Int, userId: Int, isPresent: Boolean = false, isAdmin: Boolean = false, isModerator: Boolean = false, hasPaid: Boolean = false, paymentType: Option[String] = None, receiptId: Option[String] = None, customerId: Option[String] = None, id: Option[Int] = None) {
    //For hydration
    var event: Option[Event] = None
    var user: Option[User] = None

}

case class TournamentUser(userId: Int, tournamentId: Int, isPresent: Boolean = false, isAdmin: Boolean = false, isModerator: Boolean = false, id: Option[Int] = None) {
    var tournament: Tournament = null
    var user: User = null
}

case class TournamentType(name: String = "A Tourney Type", teamPlay: Boolean = true, id: Option[Int] = None)

/*
 * The classes below are purely boilerplate so that slick will work
 * ...but may come in handy one day as well
 *
 */

case class GameTournamentType(gameId: Int = 0, tournamentTypeId: Int = 0, id: Option[Int] = None) extends DomainEntity[GameTournamentType]
case class GuildGame(guildId: Option[Int] = None, gameId: Int, id: Option[Int] = None)
