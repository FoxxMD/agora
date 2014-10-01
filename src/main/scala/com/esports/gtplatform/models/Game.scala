package models
/* In Scala namespaces are specified by packages. They work pretty much like Java */

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

/* Domain Models are specified in the models package.
 *
 * A case class differs from a regular class in that they are immutable data structures. They represent a description(constructor)
 * that holds immutable values that NEVER CHANGE. If you use a method that changes a value of a case class the object returned is
 * a totally new object. This is a foundation of FP, read up on it as it's confusing at first but eventually provides for much more
 * reasonable code.
 *
 * Case classes also have the benefit of having copy() and equality methods generated for them. You can easily compare any two case classes
 * or copy a case class simple by accessing its methods.
 *
 * Ideally all domain models are case classes, this is because domain models should be nothing more than descriptors for domain information.
 * Of course they will have setters or getters, helper methods for viewing data, etc. but they should be as agnostic as possible -- they should
 * have NO knowledge of the application, data(DAO), or business layer. This forces code written to have as little dependencies as possible which
  * facilities modularity and decoupling. */

case class Game(name: String = "A Game",publisher: String = "A Publisher",website: String = "A Website", gameType: GameType.Value = GameType.FPS, userPlay: Boolean = true, teamPlay: Boolean = true, tournamentTypes: Set[TournamentType] = Set(), id: Int = 0) {

}