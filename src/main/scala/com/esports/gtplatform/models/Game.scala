package models

object GameType extends Enumeration {
  type GameType = Value
  val FPS,MMO,RTS = Value

  def toString(gType: GameType) = gType match {
    case FPS => "FPS"
    case MMO => "MMO"
    case RTS => "RTS"
  }

  def fromString(gType: String): GameType = gType match {
    case "FPS" => FPS
    case "MMO" => MMO
    case "RTS" => RTS
  }
}

case class Game(name: String,publisher: String,website: String, gameType: GameType.Value, id: Int = 0) {

}