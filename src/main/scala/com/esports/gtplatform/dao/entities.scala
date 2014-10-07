package dao

import com.esports.gtplatform.models._
import com.googlecode.mapperdao._
import models._
import org.joda.time.{DateTimeZone, DateTime}


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
  val createdDate = column("createdDate") to { user => (user.createdDate.getMillis/1000).toInt }
  val role = column("role") to (_.role)
  val firstName = column("firstName") option (_.firstName) //option specifies that the column is NULLABLE
  val lastName = column("lastName") option (_.lastName)
  val globalHandle = column("globalHandle") to (_.globalHandle)
  val gameProfiles = onetomany(UserPlatformProfileEntity) to (_.gameProfiles)
  val teams = onetomany(GuildUserEntity) to (_.guilds)
  //there are a bunch of relationship types. MapperDao is pretty smart in determining how the FK/relationship work in the DB,
  //it just needs some direction on the type of relationship on the object.

  //Here we write the constructor MapperDao will use to return objects from information pulled from the database.
  //It uses variables assigned above with an implicit "map of values"(ValuesMap) and adds the Key if specified(with Stored)
  def constructor(implicit m: ValuesMap) = {
    val cd = new DateTime(m(createdDate)*1000L,DateTimeZone.UTC)
    new User(email, role, firstName, lastName, globalHandle, cd, id, teams, gameProfiles) with Stored {
    //override val id: Int = UserEntity.id
  }
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

  def constructor(implicit m: ValuesMap) = new User(email, role, firstName, lastName, globalHandle, createdDate, id, List[GuildUser](), List[UserPlatformProfile]()) with Stored {
  }
}

object UserPlatformProfileEntity extends Entity[Int, NoId, UserPlatformProfile]("userplatformprofile") { //some domain objects don't need a key as they are one-to-one and always attached
  val user = manytoone(UserEntity) to (_.user)
  val platform = column("platform") to (platform => GamePlatform.toString(platform.platform))
  val identifier = column("identifier") to (_.identifier)

  def constructor(implicit m: ValuesMap) = {
    val plat = GamePlatform.fromString(m(platform))
    new UserPlatformProfile(user, plat, identifier) with Stored
  }
}

object UserIdentityEntity extends Entity[Int, SurrogateIntId, UserIdentity]("useridentity") {
  val id = key("id") autogenerated (_.id)
  val user = manytoone(UserEntity) to (_.user)
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
  val createdDate = column("createdDate") to { team => (team.createdDate.getMillis/1000).toInt }
  val users = onetomany(TeamUserEntity) to (_.teamPlayers)
  val joinType = column("joinType") to (team => JoinType.toString(team.joinType))
  val tournament = manytoone(TournamentEntity) to (_.tournament)
  val isPresent = column("isPresent") to (_.isPresent)
  val guild = column("guildOnly") to (_.guildOnly)

  def constructor(implicit m: ValuesMap) = {
    val cd = new DateTime(m(createdDate)*1000L,DateTimeZone.UTC)
    val jtype = JoinType.fromString(m(joinType))
    new Team(name, jtype, tournament, users, cd, isPresent, guild, id) with Stored {
    }
  }
}


object TeamUserEntity extends Entity[Int, SurrogateIntId, TeamUser]("teams_users") {
  val id = key("id") autogenerated(_.id)
  val team = manytoone(TeamEntity) to (_.team)
  val user = manytoone(UserEntity) to (_.user)
  val captain = column("isCaptain") to (_.isCaptain)

  def constructor(implicit m: ValuesMap) = new TeamUser(team, user, captain, id) with Stored
}

object GuildUserEntity extends Entity[Int, SurrogateIntId, GuildUser]("guilds_users") {
  val id = key("id") autogenerated(_.id)
  val guild = manytoone(GuildEntity) to (_.guild)
  val user = manytoone(UserEntity) to (_.user)
  val captain = column("isCaptain") to (_.isCaptain)

  def constructor(implicit m: ValuesMap) = new GuildUser(guild, user, captain, id) with Stored
}

object GuildEntity extends Entity[Int, SurrogateIntId, Guild]("guilds") {

  val id = key("id") autogenerated(_.id)
  val name = column("name") to (_.name)
  val createdDate = column("createdDate") to { guild => (guild.createdDate.getMillis/1000).toInt }
  val users = onetomany(GuildUserEntity) to (_.members)
  val maxPlayers = column("maxPlayers") to (_.maxPlayers)
  val joinType = column("joinType") to (guild => JoinType.toString(guild.joinType))
  val games = manytomany(GameEntity) join ("guilds_games","guilds_id","games_id") to (_.games)

  def constructor(implicit m: ValuesMap) = {
    val cd = new DateTime(m(createdDate)*1000L,DateTimeZone.UTC)
    val jtype = JoinType.fromString(m(joinType))
    new Guild(name, games, maxPlayers, jtype, cd, users, id) with Stored
  }
}

object GameEntity extends Entity[Int, NaturalIntId, Game]("games"){
  val id = key("id") to (_.id)
  val name = column("name") to (_.name)
  val publisher = column("publisher") to (_.publisher)
  val website = column("website") to (_.website)
  val gt = column("gameType") to (theGame => GameType.toString(theGame.gameType))
  val uPlay = column("userPlay") to (_.userPlay)
  val tPlay = column("teamPlay") to (_.teamPlay)
  val tt = manytomany(TournamentTypeEntity) join ("games_tournamenttypes","games_id", "tournamenttypes_id") to (_.tournamentTypes)

  def constructor(implicit m: ValuesMap) = {
    val g = GameType.fromString(m(gt))
    new Game(name, publisher, website, g, uPlay, tPlay, tt, id) with Stored {
    }
  }
}

object TournamentTypeEntity extends Entity[Int, NaturalIntId, TournamentType]("tournamenttypes"){
  val id = key("id") autogenerated(_.id)
  val name = column("name") to (_.name)
  val tPlay = column("teamPlay") to (_.teamPlay)

  def constructor(implicit m: ValuesMap) = new TournamentType(name, tPlay, id) with Stored
}

object TournamentEntity extends Entity[Int, SurrogateIntId, Tournament]("tournament") {
  val id = key("id") autogenerated(_.id)
  val bt = manytoone(TournamentTypeEntity) to (_.tournamentType)
  val rt = column("registrationType") to (tourney => JoinType.toString(tourney.registrationType))
  val details = onetoonereverse(TournamentDetailsEntity) option (_.details)
  val game = manytoone(GameEntity) to (_.game)
  val event = manytoone(EventEntity) to (_.event)
  val users = onetomany(TournamentUserEntity) to (_.users)
  val teams = onetomany(TeamEntity) to (_.teams)

  def constructor(implicit m: ValuesMap) = {
    val j = JoinType.fromString(m(rt))
    new Tournament(bt, j, game, event, details, users, teams, id) with Stored{
      //val id: Int = TournamentEntity.id
    } }
}

object TournamentDetailsEntity extends Entity[Int, NoId, TournamentDetails]("tournamentdetails") {
  val tournament = onetoone(TournamentEntity) to (_.tournament)
  val name = column("name") option (_.name)
  val gamePlayed = column("gamePlayed") option (_.gamePlayed)
  val description = column("description") option (_.description)
    val location = column("location") option (_.location)
  val rules = column("rules") option (_.rules)
  val prizes = column("prizes") option (_.prizes)
  val streams = column("streams") option (_.streams)
  val servers = column("servers") option (_.servers)
  val timeStart = column("timeStart") option {details =>
    if(details.timeStart.isDefined)
      Some((details.timeStart.get.getMillis/1000).toInt)
    else
      None
  }
  val timeEnd = column("timeEnd") option {details =>
    if(details.timeEnd.isDefined)
      Some((details.timeEnd.get.getMillis/1000).toInt)
    else
      None
  }
    val maxSize = column("teamMaxSize") to (_.teamMaxSize)
    val minSize = column("teamMinSize") to (_.teamMinSize)
    val pmaxSize = column("playerMaxSize") to (_.playerMaxSize)
    val pminSize = column("playerMinSize") to (_.playerMinSize)

  def constructor(implicit m: ValuesMap) = {
    val ts = new DateTime(m(timeStart)*1000L,DateTimeZone.UTC)
    val te = new DateTime(m(timeEnd)*1000L,DateTimeZone.UTC)
    new TournamentDetails(tournament, name, gamePlayed, description, location, rules, prizes, streams, servers, Some(ts), Some(te), minSize, maxSize, pminSize, pmaxSize) with Stored
  }
}

object TournamentUserEntity extends Entity[Int, SurrogateIntId, TournamentUser]("user_tournaments") {
  val id = key("id") autogenerated(_.id)
  val tournament = manytoone(TournamentEntity) to (_.tournament)
  val user = manytoone(UserEntity) to (_.user)
  val isPresent = column("isPresent") to (_.isPresent)
  val isAdmin = column("isAdmin") to (_.isAdmin)
  val isModerator = column("isModerator") to (_.isModerator)

  def constructor(implicit m: ValuesMap) = new TournamentUser(tournament, user, isPresent, isAdmin, isModerator) with Stored
  {
    val id: Int = TournamentUserEntity.id
  }
}

object EventEntity extends Entity[Int, SurrogateIntId, Event]("events") {
  val id = key("id") autogenerated(_.id)
  val name = column("name") to (_.name)
  val eventType = column("eventType") to (event => JoinType.toString(event.joinType))
  val details = onetoonereverse(EventDetailsEntity) option (_.details)
  val users = onetomany(EventUserEntity) to (_.users)
  val payments = onetomany(EventPaymentEntity) to (_.payments)
  val tournaments = onetomany(TournamentEntity) to (_.tournaments)

  def constructor(implicit m: ValuesMap) = {
    val e = JoinType.fromString(m(eventType))
    new Event(name, e, details, payments, users, tournaments, id) with Stored}
}

object EventDetailsEntity extends Entity[Int, NoId, EventDetails]("eventdetails") {
  val event = onetoone(EventEntity) to (_.event)
  val location = column("location") option (_.locationName)
  val address = column("address") option (_.address)
  val city = column("city") option (_.city)
  val state = column("state") option (_.state)
  val description = column("description") option (_.description)
  val rules = column("rules") option (_.rules)
  val prizes = column("prizes") option (_.prizes)
  val streams = column("streams") option (_.streams)
  val servers = column("servers") option (_.servers)
  val timeStart = column("timeStart") option {details =>
    if(details.timeStart.isDefined)
      Some((details.timeStart.get.getMillis/1000).toInt)
    else
      None
  }
  val timeEnd = column("timeEnd") option {details =>
    if(details.timeEnd.isDefined)
      Some((details.timeEnd.get.getMillis/1000).toInt)
    else
      None
  }
  val scheduledEvents = column("scheduledevents") option (_.scheduledEvents)

  def constructor(implicit m: ValuesMap) = {
    val ts = new DateTime(m(timeStart)*1000L,DateTimeZone.UTC)
    val te = new DateTime(m(timeEnd)*1000L,DateTimeZone.UTC)
    new EventDetails(event, location, address, city, state, description, rules, prizes, streams, servers, Some(ts), Some(te), scheduledEvents) with Stored
  }
}

object EventPaymentEntity extends Entity[Int, SurrogateIntId, EventPayment]("eventpayments") {
  val id = key("id") autogenerated(_.id)
  val event = manytoone(EventEntity) to (_.event)
  val paytype = column("paytype") to (event => PaymentType.toString(event.payType))
  val secret = column("secret") option (_.secretKey)
  val public = column("public") option(_.publicKey)
  val add = column("address") option (_.address)
  val enabled = column("isenabled") to (_.isEnabled)
  val amount = column("amount") to (_.amount)

  def constructor(implicit m: ValuesMap) = {
    val p = PaymentType.fromString(m(paytype))
    new EventPayment(event, p, secret, public, add, amount, enabled, id) with Stored
  }
}

object EventUserEntity extends Entity[Int, SurrogateIntId, EventUser]("user_events"){
  val id = key("id") autogenerated(_.id)
  val event = manytoone(EventEntity) to (_.event)
  val user = manytoone(UserEntity) to (_.user)
  val isPresent = column("isPresent") to (_.isPresent)
  val isAdmin = column("isAdmin") to (_.isAdmin)
  val isModerator = column("isModerator") to (_.isModerator)
  val hasPaid = column("hasPaid") to (_.hasPaid)
  val receipt = column("receiptId") option (_.receiptId)

  def constructor(implicit m: ValuesMap) = new EventUser(event, user, isPresent, isAdmin, isModerator, hasPaid, receipt) with Stored
  {
    val id: Int = EventUserEntity.id
  }
}

object InviteEntity extends Entity[Int, SurrogateIntId, InviteT]("invites") {
  val id = key("id") autogenerated (_.id)

  val team = onetoone(GuildEntity) option(_.Author match {
    case t: Guild => Some(t)
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
  val time = column("createdOn") to { invite => (invite.Time.getMillis/1000).toInt }

  def constructor(implicit m: ValuesMap) = {
    //via the wonderful Kostas Kougios https://groups.google.com/d/msg/mapperdao/ECx9H4Umf_0/8py3P35juNwJ

    val userOption: Option[User] = user
    val teamOption: Option[Guild] = team
    val eventOption: Option[Event] = event
    val tourOption: Option[Tournament] = tournament
    val cr = new DateTime(m(time)*1000L,DateTimeZone.UTC)

    (userOption, teamOption, eventOption, tourOption) match {
    case (Some(u: User), Some(t: Guild), None, None) => new GuildInvite(t, u, message, cr, id) with Stored
    case (Some(u: User), None, Some(e: Event), None) => new EventInvite(e, u, message, cr, id) with Stored
    case _ => throw new Exception("Invite Entity construction is fuckin up.")
/*    case (Some(u: User), None, None, Some(t: Tournament)) => new TournamentUserInvite(t, u, message, time) with Stored {
      val id: Int = InviteEntity.id
    }*/
    }
}
}
