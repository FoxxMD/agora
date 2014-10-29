package com.esports.gtplatform.models

import models._
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Team(id: Int = 0, name: String, joinType: String, tournamentId: Int, createdDate: DateTime = DateTime.now(), isPresent: Boolean = false, guildOnly: Boolean = false, guildId: Option[Int] = None) extends Invitee with Inviteable with Requestable with TeamT {
    var tournament: Tournament = null
    var teamPlayers: List[TeamUser] = List()

  private[this] val TPListLens: SimpleLens[Team, List[TeamUser]] = SimpleLens[Team](_.teamPlayers)((t, tp) => t.copy(teamPlayers = tp))

  def addUser(u: User): Team = this applyLens TPListLens modify (_.+:(TeamUser(this, u, isCaptain = false)))

  def removeUser(u: User): Team = this applyLens TPListLens modify (_.filter(x => x.userId.id != u.id))

  def getCaptain = this.teamPlayers.find(u => u.isCaptain).get.userId

  def setCaptain(u: User): Team = {
    val modifiedTP = this.teamPlayers.map(x => x.copy(isCaptain = x.userId.id == u.id))
    this applyLens TPListLens set modifiedTP
  }
}
