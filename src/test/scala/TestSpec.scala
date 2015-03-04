package slickways

import scala.slick.driver.JdbcProfile

abstract class TestSuite(db: JdbcProfile#Backend#Database, profile: JdbcProfile) extends org.scalatest.FunSuite {
  test("H2") {
    val newTests = List(
      Test(
        1, "Category Theory", "Monad is ...?",
        "See http://en.wikipedia.org/wiki/Monad_(category_theory)",
        List(
          Choice("an endofunctor, together with two natural transformations", true),
          Choice("a structure that represents computations defined as sequences of steps", false),
          Choice("a term for Divinity or the first being, or the totality of all beings", false))),

      Test(
        2, "Probability Theory", "Probability is ...?",
        "See http://en.wikipedia.org/wiki/Probability",
        List(
          Choice("a degree to which the statement is supported by the available evidence", false),
          Choice("a measure of the likeliness that an event will occur", true),
          Choice("a measure of belief in some outcome", false)))
    )

    val repo = new SlickTestRepository(profile)

    db withTransaction { implicit s =>
      repo.init()
      val tests = repo.tests

      tests ++= newTests
      assert(tests.all === newTests)
    }
  }
}

import scala.slick.driver.H2Driver
class H2TestSuite
  extends TestSuite(
    H2Driver.simple.Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver"),
    H2Driver
  )
