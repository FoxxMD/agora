package com.esports.gtplatform.models

import ScalaBrackets.Bracket.ElimTour
import com.esports.gtplatform.business._
import com.esports.gtplatform.dao.SquerylDao._
import com.esports.gtplatform.dao.Squreyl._
import models.{BracketType, Tournament, User}

/**
 * Created by Matthew on 12/17/2014.
 */
case class Bracket(bracketTypeId: Int, order: Int, seedSize: Int, teamPlay: Boolean = false, tournamentId: Option[Int], bracketId: Option[String] = None, ownerId: Option[Int] = None, id: Option[Int] = None, private var _bracketType: Option[BracketType] = None) extends DomainEntity[Bracket] {

    private[this] val userRepo: UserRepo = new UserRepository
    private[this] val tournamentRepo: TournamentRepo = new TournamentRepository
    private[this] val mongoBracketRepo: MongoBracketRepo = new MongoBracketRepository

    def bracketType: BracketType = this._bracketType.getOrElse{
        inTransaction(bracketTypes.lookup(bracketTypeId).get)
    }
    def data: Option[ElimTour] = bracketId.fold[Option[ElimTour]](None)(mongoBracketRepo.get)
    def owner: Option[User] = ownerId.fold[Option[User]](None)(userRepo.get)
    def tournament: Option[Tournament] = tournamentId.fold[Option[Tournament]](None)(tournamentRepo.get)

    def this() = this(tournamentId = Some(0), bracketTypeId = 0, order = 0, seedSize = 0, teamPlay = false, bracketId = Some(""), ownerId = Some(0), id = Some(0))
}
