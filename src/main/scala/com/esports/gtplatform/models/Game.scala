package models

case class Game(id: Int = 0,
                name: String = "A Game",
                publisher: Option[String] = None,
                website: Option[String] = None,
                gameType: String = "Game",
                userPlay: Boolean = true,
                teamPlay: Boolean = true,
                logoFilename: Option[String] = None) {
    var tournamentTypes: Set[TournamentType] = Set()
    var ttLink: Set[GameTournamentType] = Set()
}
