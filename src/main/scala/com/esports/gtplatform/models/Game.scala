package models

case class Game(name: String = "A Game", publisher: Option[String] = None, website: Option[String] = None, gameType: String = "Game", userPlay: Boolean = true, teamPlay: Boolean = true, logoFilename: Option[String] = None, id: Option[Int] = None) {
    var tournamentTypes: Set[TournamentType] = Set()
    var ttLink: Set[GameTournamentType] = Set()
}
