package models

import com.esports.gtplatform.business._
import io.strongtyped.active.slick.models.Identifiable
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class User(email: String, createdDate: DateTime = DateTime.now(), firstName: Option[String] = None, lastName: Option[String] = None, globalHandle: String, role: String = "User", id: Option[Int] = None) extends Identifiable[User] {

    override type Id = Int
    override def withId(id: Id): User = copy(id = Some(id))

    var guilds: List[GuildUser] = List()
    var gameProfiles: List[UserPlatformProfile] = List()

  def getTournaments: List[Tournament] = ???

  def getAssociatedEvents(repo: EventUserRepo): List[EventUser] = repo.getByUser(this)
def getAssociatedTournaments(repo: TournamentUserRepo, trepo: TeamUserRepo, tourRepo: TournamentRepo, event: Option[Event] = None): List[Tournament] = {

    event match {
        case Some(e: Event) =>
            e.tournaments.filter(x =>
                x.users.exists(u => u.userId == this.id.get) ||
                x.teams.exists(u => u.teamPlayers.exists(p => p.id == this.id))).toList
        case None =>
            val tourneysTourRepo = repo.getByUser(this).map(x => x.tournament)
            val tourneysTeamRepo = trepo.getByUser(this).flatMap(x => tourRepo.get(x.team.tournamentId))
            tourneysTourRepo++tourneysTeamRepo //TODO why does this cause a recursive call?
            //allTourneys.filter(x => x.event.id == e.id)
    }
}

}


case class UserIdentity(userId: Int, userIdentifier: String, providerId: String, email: Option[String] = None, password: Option[String] = None, firstName: Option[String] = None, lastName: Option[String] = None, id: Option[Int] = None)
case class UserPlatformProfile(userId: Int, platform: String, identifier: String, id: Option[Int] = None) {
    var user: User = null
}
