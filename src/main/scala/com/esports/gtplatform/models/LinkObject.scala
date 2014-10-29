package models

import com.esports.gtplatform.models.Team

/**
 * Created by Matthew on 6/30/2014.
 */

/* Link objects exist to provide extra information about a relationship between two domain objects. Currently it's only
 * simple things like isPresent for Teams at a Tournament, or Users at an Event -- but I'm sure eventually the relationship will
  * grow more complex and require more information.
  *
  * These really should be case classes but ATM they are easier to handle as mutable datas.*/

case class TeamUser(id: Int = 0,
                    teamId: Int,
                    userId: Int,
                    isCaptain: Boolean = false) {
    var team: Team = null
    var user: User = null

}
case class GuildUser(id: Int = 0,
                     guildId: Int,
                     userId: Int,
                     isCaptain: Boolean = false) {
   var guild: Guild = null
    var user: User = null
}

case class EventUser(id: Int = 0,
                     eventId: Int,
                     userId: Int,
                     isPresent: Boolean = false,
                     isAdmin: Boolean = false,
                     isModerator: Boolean = false,
                     hasPaid: Boolean = false,
                     receiptId: Option[String] = None,
                     customerId: Option[String] = None) {
    //For hydration
    var event: Option[Event] = None
    var user: Option[User] = None

}

case class TournamentUser(id: Int = 0,
                          userId: Int,
                          tournamentId: Int,
                          isPresent: Boolean = false,
                          isAdmin: Boolean = false,
                          isModerator: Boolean = false) {
    var tournament: Tournament = null
    var user: User = null
}

case class TournamentType(id: Int = 0,
                          name: String = "A Tourney Type",
                          teamPlay: Boolean = true)

/*
 * The classes below are purely boilerplate so that slick will work
 * ...but may come in handy one day as well
 *
 */

case class GameTournamentType(gameId: Int = 0, tournamentTypeId: Int = 0, id: Int = 0)
case class GuildGame(id: Int, guildId: Int, gameId: Int)
