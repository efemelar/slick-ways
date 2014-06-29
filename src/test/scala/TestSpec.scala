package slickways

import scala.slick.jdbc.JdbcBackend.{Database, Session}

abstract class TestSuite(db: Database) extends org.scalatest.FunSuite {
  test("H2") {
    val newTests = List(
      Test(
        1, "Category Theory",
        "Monad is ...?", "See http://en.wikipedia.org/wiki/Monad_(category_theory)"),
      Test(
        2, "Probability Theory",
        "Probability is ...?", "See http://en.wikipedia.org/wiki/Probability")
    )
    val newChoices = List(
      Choice(1, "an endofunctor, together with two natural transformations", true),
      Choice(1, "a structure that represents computations defined as sequences of steps", false),
      Choice(1, "a term for Divinity or the first being, or the totality of all beings", false),

      Choice(2, "a degree to which the statement is supported by the available evidence", false),
      Choice(2, "a measure of the likeliness that an event will occur", true),
      Choice(2, "a measure of belief in some outcome", false)
    )

    db withTransaction { implicit s =>

      Schema.create()

      TestDao insert newTests
      assert(TestDao.list === newTests)

      ChoiceDao insert newChoices
      assert(ChoiceDao.list === newChoices)
    }
  }
}

class H2TestSuite
  extends TestSuite(Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver"))
