package com.esports.gtplatform.business.services

import com.escalatesoft.subcut.inject.{Injectable, BindingModule}
import com.esports.gtplatform.business.{TournamentUserRepo, TournamentTeamRepo, TeamRepo}
import models._
import org.slf4j.LoggerFactory

/**
 * Created by Matthew on 9/15/2014.
 */
class TeamService(implicit val bindingModule: BindingModule) extends Injectable with GenericService[Team] {
  val logger = LoggerFactory.getLogger(getClass)
  val teamRepo = inject[TeamRepo]

 override def isUnique(obj: Team): Option[String] = ??? ///teamRepo.getByName(obj.name).isDefined
  //def isUnique(obj: Team): Boolean = teamRepo.getByName(obj.name).isDefined
  override def create(obj: Team): Any = ???

  def getTournamentTeams(t: Team): List[TournamentTeam] = {
    val ttRepo = inject[TournamentTeamRepo]
    ttRepo.getByTeam(t)
  }
  def getEvents(t: Team): List[Event] = {
    getTournamentTeams(t).map(x => x.tournament.event).distinct
  }
}
