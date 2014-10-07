package com.esports.gtplatform.models

import models._
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Team(
            name: String,
            joinType: JoinType.Value,
            tournament: Tournament,
            teamPlayers: List[TeamUser] = List(),
            createdDate: DateTime = DateTime.now(),
            isPresent: Boolean = false,
            guildOnly: Boolean = false,
            id: Int = 0) extends Invitee with Inviteable with Requestable with TeamT {

  private[this] val TPListLens: SimpleLens[Team, List[TeamUser]] = SimpleLens[Team](_.teamPlayers)((t, tp) => t.copy(teamPlayers = tp))

  def addUser(u: User): Team = this applyLens TPListLens modify (_.+:(TeamUser(this, u, isCaptain = false)))

  def removeUser(u: User): Team = this applyLens TPListLens modify (_.filter(x => x.user.id != u.id))

  def getCaptain = this.teamPlayers.find(u => u.isCaptain).get.user

  def setCaptain(u: User): Team = {
    val modifiedTP = this.teamPlayers.map(x => x.copy(isCaptain = x.user == u))
    this applyLens TPListLens set modifiedTP
  }
}
