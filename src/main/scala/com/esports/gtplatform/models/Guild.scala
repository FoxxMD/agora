package models

import com.esports.gtplatform.business.TeamRepo
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Guild(name: String, description: Option[String] = None, maxPlayers: Option[Int], joinType: String = "Public", createdDate: DateTime = DateTime.now(), id: Option[Int] = None) {

    var games: List[Game] = List()
    var gamesLink: List[GuildGame] = List()
    var members: List[GuildUser] = List()

  def getCaptain = this.members.find(u => u.isCaptain).get.user



    def getTournaments(teamRepo: TeamRepo, event: Option[Event] = None): List[Tournament]  = {
        event match {
            case Some(e: Event) =>
                e.tournaments.filter(x => x.teams.exists(u => u.guildId.getOrElse(false) == this.id.get)).toList
            case None =>
                teamRepo.getByGuild(this.id.get).map(x => x.tournament)
        }
    }
}
