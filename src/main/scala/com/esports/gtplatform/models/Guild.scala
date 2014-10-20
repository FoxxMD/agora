package models

import com.esports.gtplatform.business.{TeamRepo, TournamentRepo}
import com.esports.gtplatform.models._
import models.JoinType.JoinType
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Guild(
            name: String,
            games: List[Game],
            maxPlayers: Int,
            joinType: JoinType.Value,
            createdDate: DateTime = DateTime.now(),
            members: List[GuildUser] = List(),
            id: Int = 0) extends Invitee with Inviteable with Requestable with GuildT {

  private[this] val TPListLens: SimpleLens[Guild, List[GuildUser]] = SimpleLens[Guild](_.members)((t, tp) => t.copy(members = tp))
  private[this] val GamesListLens: SimpleLens[Guild, List[Game]] = SimpleLens[Guild](_.games)((t, g) => t.copy(games = g))

  def addUser(u: User): Guild = this applyLens TPListLens modify (_.+:(GuildUser(this, u, isCaptain = false)))

  def removeUser(u: User): Guild = this applyLens TPListLens modify (_.filter(x => x.user.id != u.id))

  def getCaptain = this.members.find(u => u.isCaptain).get.user

  def setCaptain(u: User): Guild = {
    val modifiedTP = this.members.map(x => x.copy(isCaptain = x.user == u))
    this applyLens TPListLens set modifiedTP
  }

  def addGame(g: Game): Guild = this applyLens GamesListLens modify (_.+:(g))

  def removeGame(g: Game): Guild = this applyLens GamesListLens modify(_.filter(x => x != g))

    def getTournaments(teamRepo: TeamRepo, event: Option[Event] = None): List[Tournament]  = {
        event match {
            case Some(e: Event) =>
                e.tournaments.filter(x => x.teams.exists(u => u.guildId.getOrElse(false) == this.id)).toList
            case None =>
                teamRepo.getByGuild(this.id).map(x => x.tournament)
        }
    }
}
