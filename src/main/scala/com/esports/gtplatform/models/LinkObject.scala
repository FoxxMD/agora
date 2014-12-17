package models

import com.esports.gtplatform.business.{GuildRepository, UserRepository}
import com.esports.gtplatform.models.{DomainEntity, Team}

/**
 * Created by Matthew on 6/30/2014.
 */

import com.esports.gtplatform.dao.SquerylDao._
import com.esports.gtplatform.dao.Squreyl._

/* Link objects exist to provide extra information about a relationship between two domain objects. Currently it's only
 * simple things like isPresent for Teams at a Tournament, or Users at an Event -- but I'm sure eventually the relationship will
  * grow more complex and require more information.
  *
  * These really should be case classes but ATM they are easier to handle as mutable datas.*/

case class TeamUser(teamId: Option[Int], userId: Int, isCaptain: Boolean = false, id: Option[Int] = None) extends DomainEntity[TeamUser] {
    lazy val team: Team = inTransaction { teams.lookup(teamId.get).get }
    lazy val user: User = inTransaction { users.lookup(userId).get }

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(teamId = Some(0), userId = 0, isCaptain = false, id = Some(0))

}
case class GuildUser(guildId: Option[Int], userId: Int, isCaptain: Boolean = false, id: Option[Int] = None) extends DomainEntity[GuildUser] {
/*    implicit object guildKED extends KeyedEntityDef[Guild, Option[Int]] {
        def getId(g: Guild) = g.id
        def isPersisted(g: Guild) = g.id.isDefined && g.id.get != 0
        def idPropertyName = "id"
    }*/
    private[this] val guildRepo = new GuildRepository
    private[this] val userRepo = new UserRepository
   lazy val guild: Guild = guildRepo.get(guildId.get).get
    lazy val user: User = userRepo.get(userId).get

    def this() = this(guildId = Some(0), userId = 0, isCaptain = false, id = Some(0))
}

case class EventUser(
                        eventId: Int,
                        userId: Int,
                        isPresent: Boolean = false,
                        isAdmin: Boolean = false,
                        isModerator: Boolean = false,
                        hasPaid: Boolean = false,
                        paymentType: Option[String] = None,
                        receiptId: Option[String] = None,
                        customerId: Option[String] = None,
                        id: Option[Int] = None,
                        private var _event: Option[Event] = None,
                        private var _user: Option[User] = None) extends DomainEntity[EventUser] {
    //For hydration
    def event: Option[Event] = if(this._event.isDefined)
    {
        _event
    } else inTransaction {
        _event = events.lookup(this.eventId).headOption
        _event
    }
    //def event_=(e: Option[Event]) { _event = e }
    def user: Option[User] = if(this._user.isDefined) {
        _user
    } else inTransaction {
        _user = users.lookup(this.userId).headOption
        _user
    }
    def this() = this(
        eventId = 0,
        userId = 0,
        isPresent = false,
        isAdmin = false,
        isModerator = false,
        hasPaid = false,
        paymentType = Some(""),
        receiptId = Some(""),
        customerId = Some(""),
        id = Some(0))

}

case class TournamentUser(userId: Int, tournamentId: Int, isPresent: Boolean = false, isAdmin: Boolean = false, isModerator: Boolean = false, id: Option[Int] = None) extends DomainEntity[TournamentUser] {
    var tournament: Tournament = null
    lazy val user: User = inTransaction { users.lookup(userId).get }

    def this() = this(userId = 0, tournamentId = 0, isPresent = false, isAdmin = false, isModerator = false, id = Some(0))
}

case class BracketType(name: String = "A Tourney Type", teamPlay: Boolean = true, id: Option[Int] = None) extends DomainEntity[BracketType] {
    def this() = this(name = "", teamPlay = true, id = Some(0))
}

case class TournamentBracket(tournamentId: Int, bracketTypeId: Int, order: Int, bracketId: Option[String] = None, id: Option[Int] = None, private var _bracketTypes: Option[List[BracketType]] = None) {

    def bracketType: List[BracketType] = this._bracketTypes.getOrElse{
        inTransaction(bracketTypeToTournamentBrackets.right(this).toList)
    }

    def this() = this(tournamentId = 0, bracketTypeId = 0, order = 0, bracketId = Some(""), id = Some(0))
}

case class GameBracketType(gameId: Int = 0, bracketTypeId: Int = 0, id: Option[Int] = None) extends DomainEntity[GameBracketType] {
    def this() = this(gameId = 0, bracketTypeId = 0, id = Some(0))
}
case class GuildGame(guildId: Option[Int] = None, gameId: Int, id: Option[Int] = None) extends DomainEntity[GuildGame] {
    def this() = this(guildId = Some(0), gameId = 0, id = Some(0))
}
