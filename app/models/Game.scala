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

class Game(val id: Int,val name: String,val publisher: String,val website: String,val gameType: GameType.Value)
