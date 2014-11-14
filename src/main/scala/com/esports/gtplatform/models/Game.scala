package models

import com.esports.gtplatform.models.DomainEntity

case class Game(name: String = "A Game",
                publisher: Option[String] = None,
                website: Option[String] = None,
                gameType: String = "Game",
                userPlay: Boolean = true,
                teamPlay: Boolean = true,
                logoFilename: Option[String] = None, id: Option[Int] = None) extends DomainEntity[Game] {
    var tournamentTypes: Set[TournamentType] = Set()
    var ttLink: Set[GameTournamentType] = Set()

    def this() = this("",Some(""),Some(""),"",true,true,Some(""),Some(0))
}
