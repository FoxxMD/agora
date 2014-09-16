package com.esports.gtplatform.business.services

import com.escalatesoft.subcut.inject.{Injectable, BindingModule}
import com.esports.gtplatform.business.{TournamentUserRepo, GuildRepo}
import models._
import org.slf4j.LoggerFactory

/**
 * Created by Matthew on 9/15/2014.
 */
class TeamService(implicit val bindingModule: BindingModule) extends Injectable with GenericService[Guild] {
  val logger = LoggerFactory.getLogger(getClass)
  val teamRepo = inject[GuildRepo]

 override def isUnique(obj: Guild): Option[String] = ??? ///teamRepo.getByName(obj.name).isDefined
  //def isUnique(obj: Team): Boolean = teamRepo.getByName(obj.name).isDefined
  override def create(obj: Guild): Any = ???

/*  def getTournamentTeams(t: Guild): List[TournamentTeam] = {
    val ttRepo = inject[TournamentTeamRepo]
    ttRepo.getByTeam(t)
  }
  def getEvents(t: Guild): List[Event] = {
    getTournamentTeams(t).map(x => x.tournament.event).distinct
  }*/
}
