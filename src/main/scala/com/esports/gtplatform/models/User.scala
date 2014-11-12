package models

import com.esports.gtplatform.business._
import com.esports.gtplatform.models.Invitee
import io.strongtyped.active.slick.models.Identifiable

//import models.GamePlatform.GamePlatform
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

//object GamePlatform extends Enumeration {
//  type GamePlatform = Value
//  val Steam, Battlenet, Riot = Value
//
//  def toString(j: GamePlatform) = j match {
//    case Steam => "Steam"
//    case Battlenet => "Battle.net"
//    case Riot => "Riot"
//  }
//
//  def fromString(j: String): GamePlatform = j match {
//    case "Steam" => Steam
//    case "Battle.net" => Battlenet
//    case "Riot" => Riot
//  }
//}

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
                x.users.exists(u => u.userId == this.id) ||
                x.teams.exists(u => u.teamPlayers.exists(p => p.id == this.id))).toList
        case None =>
            val tourneysTourRepo = repo.getByUser(this).map(x => x.tournamentId)
            val tourneysTeamRepo = trepo.getByUser(this.id).flatMap(x => tourRepo.get(x.team.tournamentId))
            tourneysTourRepo++tourneysTeamRepo //TODO why does this cause a recursive call?
            //allTourneys.filter(x => x.event.id == e.id)
    }
}

  private[this] val GameProfilesLens: SimpleLens[User, List[UserPlatformProfile]] = SimpleLens[User](_.gameProfiles)((u, newProfiles) => u.copy(gameProfiles = newProfiles))

  def addGameProfile(gp: UserPlatformProfile): User = this applyLens GameProfilesLens modify(_.+:(gp))
  def removeGameProfile(ptype: String): User = this applyLens GameProfilesLens modify(_.filter(x => x.platform != ptype))
  def changeGameProfile(gp: UserPlatformProfile) = this.removeGameProfile(gp.platform).addGameProfile(gp)

  //TODO work on related entities
  //def getTeams: List[TeamUser] = queryDao.query(select from TeamUserEntity where TeamUserEntity.user. === this.id).map(x => x.team)
}


/* UserIdentity is a descriptor for a user's login credentials. It's separated from the main user because blah blah decoupling.
*
* I've left fields OAuth implementation needs.*/
case class UserIdentity(userId: Int, userIdentifier: String, providerId: String, email: Option[String] = None, password: Option[String] = None, firstName: Option[String] = None, lastName: Option[String] = None, id: Option[Int] = None)

/* Right now more of a placeholder than anything. This will eventually serve as a list linked popular game profiles for this user
* EX Steam, Battle.NET, etc. etc. */
case class UserPlatformProfile(userId: Int, platform: String, identifier: String, id: Option[Int] = None) {
    var user: User = null
}
