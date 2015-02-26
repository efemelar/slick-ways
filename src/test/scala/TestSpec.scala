package slickways

import scala.slick.driver.JdbcProfile

abstract class TestSuite(db: JdbcProfile#Backend#Database, profile: JdbcProfile) extends org.scalatest.FunSuite {
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
    val schema = new Schema(profile)
    val testDao = new TestDao(schema)
    val choiceDao = new ChoiceDao(schema)
    db withTransaction { implicit s =>

      schema.create()

      testDao insert newTests
      assert(testDao.list === newTests)

      choiceDao insert newChoices
      assert(choiceDao.list === newChoices)
    }
  }
}

import scala.slick.driver.H2Driver
class H2TestSuite
  extends TestSuite(
    H2Driver.simple.Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver"),
    H2Driver
  )
