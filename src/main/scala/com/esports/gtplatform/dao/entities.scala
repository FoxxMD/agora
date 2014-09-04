package dao

import com.esports.gtplatform.models.{EventInvite, InviteT, TeamInvite}
import com.googlecode.mapperdao._
import models._


/**
 * Created by Matthew on 6/30/2014.
 */

/* This is where we map between MySQL and our domain objects, used by MapperDao to store data and then construct objects when we pull from the DB.
 * Each domain object has one Entity.
 */

object UserEntity  extends Entity[Int, SurrogateIntId, User]("users") {
  //Int = the datatype of the primary key
  //SurrogateIntId -- The type of key strategy. Surrogate = No key on domain object, Natural = Key on domain object.
  //User = The domain object being mapped to
  //("users") -- specifies the name of the table(if different from name of domain object)

  val id = key("id") autogenerated (_.id) //key is "id" in DB, specifies it is autogenerated
  val email = column("email") to (_.email) //specify the column in the table and what property on the object it maps to
  val createdDate = column("createdDate") to (_.createdDate)
  val role = column("role") to (_.role)
  val firstName = column("firstName") option (_.firstName) //option specifies that the column is NULLABLE
  val lastName = column("lastName") option (_.lastName)
  val globalHandle = column("globalHandle") to (_.globalHandle)
  val gameProfiles = onetomany(UserPlatformProfileEntity) to (_.gameProfiles)
  val teams = onetomany(TeamUserEntity) to (_.teams)
  //there are a bunch of relationship types. MapperDao is pretty smart in determining how the FK/relationship work in the DB,
  //it just needs some direction on the type of relationship on the object.

  //Here we write the constructor MapperDao will use to return objects from information pulled from the database.
  //It uses variables assigned above with an implicit "map of values"(ValuesMap) and adds the Key if specified(with Stored)
  def constructor(implicit m: ValuesMap) = new User(email, role, firstName, lastName, globalHandle, createdDate, id, teams) with Stored {
    //override val id: Int = UserEntity.id
    this.gameProfiles = gameProfiles
  }
}
object NonActiveUserEntity extends Entity[Int, SurrogateIntId, User]("nonactiveusers") {

  //override val databaseSchema = Some(Schema("nonactive"))

  val id = key("id") autogenerated (_.id)
  val email = column("email") to (_.email)
  val createdDate = column("createdDate") to (_.createdDate)
  val role = column("role") to (_.role)
  val firstName = column("firstName") option (_.firstName)
  val lastName = column("lastName") option (_.lastName)
  val globalHandle = column("globalHandle") to (_.globalHandle)
  //val gameProfiles = onetomany(UserPlatformProfileEntity) to (_.gameProfiles)
  //val teams = onetomany(TeamUserEntity) to (_.teams)

  def constructor(implicit m: ValuesMap) = new User(email, role, firstName, lastName, globalHandle, createdDate, id, List[TeamUser]()) with Stored {

    this.gameProfiles = List[UserPlatformProfile]()
  }
}

object UserPlatformProfileEntity extends Entity[Int, NoId, UserPlatformProfile]("userplatformprofile") { //some domain objects don't need a key as they are one-to-one and always attached
  val user = manytoone(UserEntity) to (_.user)
  val platform = column("platform") to (platform => Platform.toString(platform.platform))
  val identifier = column("identifier") to (_.identifier)

  def constructor(implicit m: ValuesMap) = {
    val plat = Platform.fromString(m(platform))
    new UserPlatformProfile(user, plat, identifier) with Stored
  }
}

object UserIdentityEntity extends Entity[Int, SurrogateIntId, UserIdentity]("useridentity") {
  val id = key("id") autogenerated (_.id)
  val user = manytoone(UserEntity) foreignkey "userId" to (_.user)
  val userId = column("userIdentifier") to (_.userId)
  val firstName = column("firstName") option (_.firstName)
  val lastName = column("lastName") option (_.lastName)
  val providerId = column("providerId") to (_.providerId)
  val email = column("email") option (_.email)
  val password = column("password") to (_.password)

  def constructor(implicit m: ValuesMap) = {
    new UserIdentity(user, userId, providerId, firstName, lastName, Option(firstName + " " + lastName), email, None, password, id) with Stored
/*    {
      val id: Int = UserIdentityEntity.id
    }*/
  }
}

object NonActiveUserIdentityEntity extends Entity[Int, SurrogateIntId, UserIdentity]("nonactiveuseridentity") {

  //override val databaseSchema = Some(Schema("nonactive"))

  val id = key("id") autogenerated (_.id)
  val user = manytoone(NonActiveUserEntity) foreignkey "userId" to (_.user)
  val userId = column("userIdentifier") to (_.userId)
  val firstName = column("firstName") option (_.firstName)
  val lastName = column("lastName") option (_.lastName)
  val providerId = column("providerId") to (_.providerId)
  val email = column("email") option (_.email)
  val password = column("password") to (_.password)

  def constructor(implicit m: ValuesMap) = {
    new UserIdentity(user, userId, providerId, firstName, lastName, Option(firstName + " " + lastName), email, None, password, id) with Stored
  }
}

object TeamEntity extends Entity[Int, SurrogateIntId, Team]("teams") {

  val id = key("id") autogenerated(_.id)
  val name = column("name") to (_.name)
  val createdDate = column("createdDate") to (_.createdDate)
  val users = onetomany(TeamUserEntity) to (_.teamPlayers)
  val games = manytomany(GameEntity) join ("teams_games","team_id","game_id") to (_.games)

  def constructor(implicit m: ValuesMap) = new Team(name, createdDate, games, users, id) with Stored {
   // val id: Int = TeamEntity.id
  }
}


object TeamUserEntity extends Entity[Int, SurrogateIntId, TeamUser]("teams_users") {
  val id = key("id") autogenerated(_.id)
  //val team = manytoone(TeamEntity) foreignkey "teamId" to (_.team)
  val team = manytoone(TeamEntity) to (_.team)
  //val user = manytoone(UserEntity) foreignkey "userId" to (_.user)
  val user = manytoone(UserEntity) to (_.user)
  val captain = column("isCaptain") to (_.isCaptain)

  def constructor(implicit m: ValuesMap) = new TeamUser(team, user, captain) with Stored
  {
    val id: Int = TeamUserEntity.id
  }
}

object GameEntity extends Entity[Int, NaturalIntId, Game]("games"){
  val id = key("id") autogenerated (_.id)
  val name = column("name") to (_.name)
  val publisher = column("publisher") to (_.publisher)
  val website = column("website") to (_.website)
  val gt = column("gameType") to (theGame => GameType.toString(theGame.gameType))

  def constructor(implicit m: ValuesMap) = {
    val g = GameType.fromString(m(gt))
    new Game(name, publisher, website, g, id) with Stored {
      //val id: Int = GameEntity.id
    }
  }
}

object TournamentEntity extends Entity[Int, SurrogateIntId, Tournament]("tournament") {
  val id = key("id") autogenerated(_.id)
  val bt = column("bracketType") to (tourney => BracketType.toString(tourney.bracketType))
  val rt = column("registrationType") to (tourney => JoinType.toString(tourney.registrationType))
  val details = onetoonereverse(TournamentDetailsEntity) to (_.details)
  val game = manytoone(GameEntity) foreignkey "game_id" to (_.game)
  val event = manytoone(EventEntity) foreignkey "events_id" to (_.event)
  val users = onetomany(TournamentUserEntity) to (_.users)
  val teams = onetomany(TournamentTeamEntity) to (_.teams)

  def constructor(implicit m: ValuesMap) = {
    val j = JoinType.fromString(m(rt))
    val b = BracketType.fromString(m(bt))
    new Tournament(b, j, game, event, details, users, teams, id) with Stored{
      //val id: Int = TournamentEntity.id
    } }
}

object TournamentDetailsEntity extends Entity[Int, NoId, TournamentDetails]("tournamentdetails") {
  val tournament = onetoone(TournamentEntity) foreignkey "tournamentId" to (_.tournament)
  val name = column("name") option (_.name)
  val gamePlayed = column("gamePlayed") option (_.gamePlayed)
  val description = column("description") option (_.description)
  val rules = column("rules") option (_.rules)
  val prizes = column("prizes") option (_.prizes)
  val streams = column("streams") option (_.streams)
  val servers = column("servers") option (_.servers)
  val timeStart = column("timeStart") to (_.timeStart)
  val timeEnd = column("timeEnd") to (_.timeEnd)

  def constructor(implicit m: ValuesMap) = new TournamentDetails(tournament, name, gamePlayed, description, rules, prizes, streams, servers, timeStart, timeEnd) with Stored
}

object TournamentUserEntity extends Entity[Int, SurrogateIntId, TournamentUser]("user_tournaments") {
  val id = key("id") autogenerated(_.id)
  val tournament = manytoone(TournamentEntity) foreignkey "tournamentId" to (_.tournament)
  val user = manytoone(UserEntity) foreignkey "userId" to (_.user)
  val isPresent = column("isPresent") to (_.isPresent)
  val isAdmin = column("isAdmin") to (_.isAdmin)
  val isModerator = column("isModerator") to (_.isModerator)

  def constructor(implicit m: ValuesMap) = new TournamentUser(tournament, user, isPresent, isAdmin, isModerator) with Stored
  {
    val id: Int = TournamentUserEntity.id
  }
}

object TournamentTeamEntity extends Entity[Int, SurrogateIntId, TournamentTeam]("teams_tournaments") {
  val id = key("id") autogenerated(_.id)
  val tournament = manytoone(TournamentEntity) foreignkey "tournamentId" to (_.tournament)
  val team = manytoone(TeamEntity) foreignkey "teamId" to (_.team)
  val isPresent = column("isPresent") to (_.isPresent)

  def constructor(implicit m: ValuesMap) = new TournamentTeam(tournament, team, isPresent) with Stored{
    val id: Int = TournamentTeamEntity.id
  }
}

object EventEntity extends Entity[Int, SurrogateIntId, Event]("events") {
  val id = key("id") autogenerated(_.id)
  val name = column("name") to (_.name)
  val eventType = column("eventType") to (event => JoinType.toString(event.joinType))
  val details = onetoonereverse(EventDetailsEntity) option (_.details)
  val users = onetomany(EventUserEntity) to (_.users)
  val tournaments = onetomany(TournamentEntity) to (_.tournaments)

  def constructor(implicit m: ValuesMap) = {
    val e = JoinType.fromString(m(eventType))
    new Event(name, e, details, users, tournaments, id) with Stored}
}

object EventDetailsEntity extends Entity[Int, NoId, EventDetails]("eventdetails") {
  val event = onetoone(EventEntity) to (_.event)
  val address = column("address") option (_.address)
  val city = column("city") option (_.city)
  val state = column("state") option (_.state)
  val description = column("description") option (_.description)
  val rules = column("rules") option (_.rules)
  val prizes = column("prizes") option (_.prizes)
  val streams = column("streams") option (_.streams)
  val servers = column("servers") option (_.servers)
  val timeStart = column("timeStart") option (_.timeStart)
  val timeEnd = column("timeEnd") option (_.timeEnd)

  def constructor(implicit m: ValuesMap) = new EventDetails(event, address, city, state, description, rules, prizes, streams, servers, timeStart, timeEnd) with Stored
}

/*object EventDetailsEntity extends Entity[Int, NaturalIntId, EventDetails]("eventdetails") {
  //val event = onetoone(EventEntity) foreignkey "eventId" to (_.event)
  val id = key("id") to (_.id)
  val address = column("address") option (_.address)
  val city = column("city") option (_.city)
  val state = column("state") option (_.state)
  val description = column("description") option (_.description)
  val rules = column("rules") option (_.rules)
  val prizes = column("prizes") option (_.prizes)
  val streams = column("streams") option (_.streams)
  val servers = column("servers") option (_.servers)
  val timeStart = column("timeStart") option (_.timeStart)
  val timeEnd = column("timeEnd") option (_.timeEnd)

  def constructor(implicit m: ValuesMap) = new EventDetails(address, city, state, description, rules, prizes, streams, servers, timeStart, timeEnd, id) with Stored
}*/

object EventUserEntity extends Entity[Int, SurrogateIntId, EventUser]("user_events"){
  val id = key("id") autogenerated(_.id)
  val event = manytoone(EventEntity) foreignkey "events_id" to (_.event)
  val user = manytoone(UserEntity) foreignkey "userId" to (_.user)
  val isPresent = column("isPresent") to (_.isPresent)
  val isAdmin = column("isAdmin") to (_.isAdmin)
  val isModerator = column("isModerator") to (_.isModerator)

  def constructor(implicit m: ValuesMap) = new EventUser(event, user, isPresent, isAdmin, isModerator) with Stored
  {
    val id: Int = EventUserEntity.id
  }
}

object InviteEntity extends Entity[Int, SurrogateIntId, InviteT]("invites") {
  val id = key("id") autogenerated (_.id)

  val team = onetoone(TeamEntity) option(_.Author match {
    case t: Team => Some(t)
    case _ => None
  })
  val event = manytoone(EventEntity) option(_.Author match {
    case e: Event => Some(e)
    case _ => None
  })
  val user = manytoone(UserEntity) option(_.Author match {
    case u: User => Some(u)
    case _ => None
  })
  val tournament = manytoone(TournamentEntity) option(_.Author match {
    case t: Tournament => Some(t)
    case _ => None
  })
  val message = column("message") to (_.message)
  val time = column("createdOn") to (_.Time)

  def constructor(implicit m: ValuesMap) = {
    //via the wonderful Kostas Kougios https://groups.google.com/d/msg/mapperdao/ECx9H4Umf_0/8py3P35juNwJ

    val userOption: Option[User] = user
    val teamOption: Option[Team] = team
    val eventOption: Option[Event] = event
    val tourOption: Option[Tournament] = tournament

    (userOption, teamOption, eventOption, tourOption) match {
    case (Some(u: User), Some(t: Team), None, None) => new TeamInvite(t, u, message, time) with Stored {
      val id: Int = InviteEntity.id
    }
    case (Some(u: User), None, Some(e: Event), None) => new EventInvite(e, u, message, time) with Stored {
      val id: Int = InviteEntity.id
    }
    case _ => throw new Exception("Invite Entity construction is fuckin up.")
/*    case (Some(u: User), None, None, Some(t: Tournament)) => new TournamentUserInvite(t, u, message, time) with Stored {
      val id: Int = InviteEntity.id
    }*/
    }
}
}
