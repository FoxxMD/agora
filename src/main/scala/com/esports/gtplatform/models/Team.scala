package com.esports.gtplatform.models

import models._
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Team(name: String, joinType: String, tournamentId: Int, createdDate: DateTime = DateTime.now(), isPresent: Boolean = false, guildOnly: Boolean = false, guildId: Option[Int] = None, id: Option[Int] = None) {
    var tournament: Tournament = null
    var teamPlayers: List[TeamUser] = List()


  def getCaptain = this.teamPlayers.find(u => u.isCaptain).get.user
}
