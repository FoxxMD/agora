package models

import com.esports.gtplatform.models.{GroupT, Inviteable, Invitee, Requestable}
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Team(
            name: String,
            createdDate: DateTime,
            games: List[Game],
            teamPlayers: List[TeamUser] = List(),
            id: Int = 0) extends Invitee with Inviteable with Requestable with GroupT[Team]
{
  private[this] val TPListLens: SimpleLens[Team, List[TeamUser]] = SimpleLens[Team](_.teamPlayers)((t,tp) => t.copy(teamPlayers = tp))
  private[this] val CaptainLens: SimpleLens[TeamUser, Boolean] = SimpleLens[TeamUser](_.isCaptain)((tu, cap) => tu.copy(isCaptain = cap))

  override def addUser(u: User): Team = this applyLens TPListLens modify(_.+:(TeamUser(this,u,isCaptain = false)))

  override def removeUser(u: User): Team = this applyLens TPListLens modify(_.filter(x => x.user != u))

  def getCaptain = this.teamPlayers.find(u => u.isCaptain).get.user

  def setCaptain(u: User) = ???//this applyLens TPListLens set(this.teamPlayers.)//each[List[TeamUser], TeamUser].modify(this.teamPlayers, _.copy())//this applyLens TPListLens multiLift(_.)
}
