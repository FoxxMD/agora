package models

import com.esports.gtplatform.models.DomainEntity

case class Game(name: String = "A Game",
                publisher: Option[String] = None,
                website: Option[String] = None,
                gameType: String = "Game",
                userPlay: Boolean = true,
                teamPlay: Boolean = true,
                logoFilename: Option[String] = None, id: Option[Int] = None) extends DomainEntity[Game] {

    import com.esports.gtplatform.dao.SquerylDao._

    lazy val tournamentTypes = gameBracketRelation.left(this).iterator.toList

    def this() = this("",Some(""),Some(""),"",true,true,Some(""),Some(0))
}
