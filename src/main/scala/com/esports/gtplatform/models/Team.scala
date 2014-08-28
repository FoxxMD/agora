package models

import com.esports.gtplatform.models._
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Team(
            name: String,
            createdDate: DateTime = DateTime.now(),
            games: List[Game],
            teamPlayers: List[TeamUser] = List(),
            id: Int = 0) extends Invitee with Inviteable with Requestable with TeamT {

  private[this] val TPListLens: SimpleLens[Team, List[TeamUser]] = SimpleLens[Team](_.teamPlayers)((t, tp) => t.copy(teamPlayers = tp))
  private[this] val GamesListLens: SimpleLens[Team, List[Game]] = SimpleLens[Team](_.games)((t, g) => t.copy(games = g))

  def addUser(u: User): Team = this applyLens TPListLens modify (_.+:(TeamUser(this, u, isCaptain = false)))

  def removeUser(u: User): Team = this applyLens TPListLens modify (_.filter(x => x.user != u))

  def getCaptain = this.teamPlayers.find(u => u.isCaptain).get.user

  def setCaptain(u: User): Team = {
    val modifiedTP = this.teamPlayers.map(x => x.copy(isCaptain = x.user == u))
    this applyLens TPListLens set modifiedTP
  }

  def addGame(g: Game): Team = this applyLens GamesListLens modify (_.+:(g))

  def removeGame(g: Game): Team = this applyLens GamesListLens modify(_.filter(x => x != g))
}
