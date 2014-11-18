package models

import com.esports.gtplatform.business._
import com.esports.gtplatform.models.DomainEntity
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class User(email: String, createdDate: DateTime = DateTime.now(), firstName: Option[String] = None, lastName: Option[String] = None, globalHandle: String, role: String = "User", id: Option[Int] = None) extends DomainEntity[User] {

    import com.esports.gtplatform.dao.SquerylDao._
    import com.esports.gtplatform.dao.Squreyl._

    private[this] val eventUserRepo: EventUserRepo = new EventUserRepository
    private[this] val tournamentRepo: TournamentRepo = new TournamentRepository

    private[this] var _guilds: Option[List[GuildUser]] = None
    private[this] var _tourUsers: Option[List[TournamentUser]] = None
    private[this] var _teams: Option[List[TeamUser]] = None
    private[this] var _events: Option[List[EventUser]] = None
    private[this] var _profiles: Option[List[UserPlatformProfile]] = None


    def guilds: List[GuildUser] = _guilds.getOrElse[List[GuildUser]] {
        this._guilds = Option(inTransaction(guildToUsers.right(this).associations.toList))
        this._guilds.get
    }
    def guilds(g: List[GuildUser]) = this._guilds = Option(g)

    def gameProfiles: List[UserPlatformProfile] = _profiles.getOrElse[List[UserPlatformProfile]] {
        this._profiles = Option(inTransaction(userToPlatforms.left(this).iterator.toList))
        this._profiles.get
    }

    def gameProfiles(p: List[UserPlatformProfile]) = this._profiles = Option(p)


    def events: List[EventUser] = _events.getOrElse[List[EventUser]] {
        this._events = Option(eventUserRepo.getByUserHydrated(this.id.get))
        this._events.get
    }
    def events(e: List[EventUser]) = {
        this._events = Option(e)
    }

    def tournaments(event: Option[Event] = None): List[Tournament] = {

        _tourUsers.getOrElse[List[TournamentUser]] {
            this._tourUsers = Option(inTransaction(tournamentToTournamentUsers.right(this).associations.toList))
            this._tourUsers.get

        }
        _teams.getOrElse[List[TeamUser]] {
            this._teams = Option(inTransaction(teamToUsers.right(this).associations.toList))
            this._teams.get
        }

        _tourUsers.get.map(x => tournamentRepo.get(x.tournamentId).get) ++ _teams.get.map(x => tournamentRepo.get(x.team.tournamentId).get)
    }
    def tournaments(tourUsers: List[TournamentUser]) = {
        this._tourUsers = Option(tourUsers)

    }
    def teams(teamUsers: List[TeamUser]) = {
        this._teams = Option(teamUsers)
    }

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(email = "", createdDate = DateTime.now, firstName = Some(""), lastName = Some(""), globalHandle = "", role = "User", id = Some(0))

}


case class UserIdentity(userId: Int, userIdentifier: String, providerId: String, email: Option[String] = None, password: Option[String] = None, firstName: Option[String] = None, lastName: Option[String] = None, id: Option[Int] = None) extends DomainEntity[UserIdentity] {
    def this() = this(userId = 0, userIdentifier = "", providerId = "", email = Some(""), password = Some(""), firstName = Some(""), lastName = Some(""), id = Some(0))
}

case class UserPlatformProfile(userId: Int, platform: String, identifier: String, id: Option[Int] = None) extends DomainEntity[UserPlatformProfile] {

    def this() = this(userId = 0, platform = "", identifier = "", id = Some(0))
}
