package com.esports.gtplatform.models

import com.esports.gtplatform.business.{TeamUserRepository, TeamUserRepo}
import models._
import monocle.SimpleLens
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

case class Team(name: String, joinType: String, tournamentId: Int, createdDate: DateTime = DateTime.now(), isPresent: Boolean = false, guildOnly: Boolean = false, guildId: Option[Int] = None, id: Option[Int] = None) extends DomainEntity[Team] {

    import com.esports.gtplatform.dao.Squreyl._
    import com.esports.gtplatform.dao.SquerylDao._

    private[this] val teamUserRepo: TeamUserRepo = new TeamUserRepository

    lazy val tournament: Tournament = inTransaction (tournamentToTeams.right(this).single)
    lazy val teamPlayers: List[TeamUser] = inTransaction (teamToUsers.left(this).associations.toList)


  def getCaptain = this.teamPlayers.find(u => u.isCaptain).get.user

    def this() = this(name = "", joinType = "", tournamentId = 0, createdDate = DateTime.now, isPresent = false, guildOnly = false, guildId = Some(0), id = Some(0))
}
