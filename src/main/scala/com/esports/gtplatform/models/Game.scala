package models

case class Game(name: String = "A Game",
                publisher: String = "A Publisher",
                website: String = "A Website",
                gameType: String = "Game",
                userPlay: Boolean = true,
                teamPlay: Boolean = true,
                tournamentTypes: Set[TournamentType] = Set(),
                logoFilename: Option[String] = None,
                id: Int = 0) {
}
