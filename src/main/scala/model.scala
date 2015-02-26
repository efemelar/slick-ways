package slickways

case class Test(id: Int, subject: String, question: String, explanation: String)

case class Choice(testId: Int, text: String, right: Boolean)


import scala.slick.lifted._
import scala.slick.driver.JdbcProfile

class Schema(val profile: JdbcProfile) {

  import scala.slick.model.ForeignKeyAction._
  import profile.simple._

  class Tests(t: Tag) extends Table[Test](t, "test") {
    val id          = column[Int]   ("id")
    val subject     = column[String]("subject")
    val question    = column[String]("question")
    val explanation = column[String]("explanation")

    def * = (id, subject, question, explanation) <> (Test.tupled, Test.unapply)
  }

  val Tests = TableQuery[Tests]

  class Choices(t: Tag) extends Table[Choice](t, "choice") {
    val testId = column[Int]    ("test_id")
    val text   = column[String] ("text")
    val right  = column[Boolean]("right")

    val testFk = foreignKey("choice_test_fk", testId, Tests)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (testId, text, right) <> (Choice.tupled, Choice.unapply)
  }

  val Choices = TableQuery[Choices]

  def create()(implicit s: Session) = Seq(Tests, Choices) foreach (_.ddl.create)
}

object Aliases  {
  type Session = JdbcProfile#Backend#Session
}
import Aliases._

object TestDao {
  def testDao(implicit schema: Schema, session: Session) = new TestDao
}

class TestDao(implicit schema: Schema, session: Session) {
  import schema._, profile.simple._

  def insert(tests: List[Test]) = Tests ++= tests

  def list: List[Test] = Tests.list
}

object ChoiceDao {
  def choiceDao(implicit schema: Schema, session: Session) = new ChoiceDao
}

class ChoiceDao(implicit schema: Schema, session: Session) {
  import schema._, profile.simple._

  def insert(choices: List[Choice])= Choices ++= choices

  def list: List[Choice] = Choices.list
}


