package models

import com.esports.gtplatform.business.{TournamentRepository, TournamentRepo, TeamRepo}
import com.esports.gtplatform.models.{Team, DomainEntity}
import org.joda.time.DateTime
import com.esports.gtplatform.dao.Squreyl._
import com.esports.gtplatform.dao.SquerylDao._

/**
 * Created by Matthew on 6/30/2014.
 */

case class Guild(name: String, description: Option[String] = None, maxPlayers: Option[Int], joinType: String = "Public", createdDate: DateTime = DateTime.now(), id: Option[Int] = None) extends DomainEntity[Guild] {

    import com.esports.gtplatform.dao.Squreyl._
    import com.esports.gtplatform.dao.SquerylDao._

    private[this] val tournamentRepo: TournamentRepo = new TournamentRepository

    var games: List[Game] = List()
    var gamesLink: List[GuildGame] = List()
    lazy val members: List[GuildUser] = inTransaction {
        guildToUsers.left(this).associations.toList
    }
    private[this] lazy val teams: List[Team] = inTransaction (guildToTeams.left(this).toList)

    def getCaptain = this.members.find(u => u.isCaptain).get.user


    def getTournaments(event: Option[Event] = None): List[Tournament] = {
        event match {
            case Some(e: Event) =>
                for (
                    x <- teams;
                    y <- tournamentRepo.get(x.tournamentId)
                    if y.eventId == e.id.get
                ) yield y
            case None =>
                for (
                    x <- teams;
                    y <- tournamentRepo.get(x.tournamentId)
                ) yield y
        }
    }

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(name = "", description = Some(""), maxPlayers = Some(0), joinType = "", createdDate = DateTime.now, id = Some(0))
}
