package models

import com.esports.gtplatform.business.{TeamUserRepo, TeamRepo, TournamentUserRepo, EventUserRepo}
import com.esports.gtplatform.models.Invitee
import models.GamePlatform.GamePlatform
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

object GamePlatform extends Enumeration {
  type GamePlatform = Value
  val Steam, Battlenet, Riot = Value

  def toString(j: GamePlatform) = j match {
    case Steam => "Steam"
    case Battlenet => "Battle.net"
    case Riot => "Riot"
  }

  def fromString(j: String): GamePlatform = j match {
    case "Steam" => Steam
    case "Battle.net" => Battlenet
    case "Riot" => Riot
  }
}

/**
 * Created by Matthew on 6/30/2014.
 */

/* I would like User to be a case class but I'm not sure how unwieldy it will be. Once I start implementing business logic for Users
 * it will be easier to tell if this can be a case class or if it must stay mutable.
 *
 * The main user class holds only a small amount of information in order to facilitate decoupling between related objects.
 */
case class User(
                 email: String,
                 role: String,
                 firstName: Option[String],
                 lastName: Option[String],
                 globalHandle: String,
                 createdDate: DateTime = DateTime.now(),
                 id: Int = 0,
                 guilds: List[GuildUser] = List(),
                 gameProfiles: List[UserPlatformProfile] = List()) extends Invitee {

  /* You'll notice on almost all classes that related objects will be accessed through a method rather than as child/parent objects.
  * This is intentional as it prevents cyclical dependencies and works better with immutable data structures.
  * */

  def getTournaments: List[Tournament] = ???

  def getAssociatedEvents(repo: EventUserRepo): List[EventUser] = repo.getByUser(this)
def getAssociatedTournaments(repo: TournamentUserRepo, trepo: TeamUserRepo, event: Option[Event] = None): List[Tournament] = {

    val allTourneys = repo.getByUser(this).map(x => x.tournament)++trepo.getByUser(this.id).map(x => x.team.tournament)
  if(allTourneys.nonEmpty)
  {
    event match {
      case Some(e: Event) =>
        allTourneys.filter(x => x.event.id == e.id)
      case None =>
        allTourneys
    }
  }
  else{
    List[Tournament]()
  }

}

  private[this] val GameProfilesLens: SimpleLens[User, List[UserPlatformProfile]] = SimpleLens[User](_.gameProfiles)((u, newProfiles) => u.copy(gameProfiles = newProfiles))

  def addGameProfile(gp: UserPlatformProfile): User = this applyLens GameProfilesLens modify(_.+:(gp))
  def removeGameProfile(ptype: GamePlatform): User = this applyLens GameProfilesLens modify(_.filter(x => x.platform != ptype))
  def changeGameProfile(gp: UserPlatformProfile) = this.removeGameProfile(gp.platform).addGameProfile(gp)

  //TODO work on related entities
  //def getTeams: List[TeamUser] = queryDao.query(select from TeamUserEntity where TeamUserEntity.user. === this.id).map(x => x.team)
}


/* UserIdentity is a descriptor for a user's login credentials. It's separated from the main user because blah blah decoupling.
*
* I've left fields OAuth implementation needs.*/
case class UserIdentity(
                         user: User,
                         providerId: String,
                         userId: String,
                         firstName: Option[String],
                         lastName: Option[String],
                         fullName: Option[String],
                         email: Option[String],
                         avatarUrl: Option[String],
                         password: String,
                         id: Int = 0)

/* Right now more of a placeholder than anything. This will eventually serve as a list linked popular game profiles for this user
* EX Steam, Battle.NET, etc. etc. */
case class UserPlatformProfile(user: User, platform: GamePlatform.Value, identifier: String)
