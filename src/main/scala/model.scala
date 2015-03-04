package slickways

case class Test(
  id: Int,
  subject: String,
  question: String,
  explanation: String,
  choices: List[Choice])

case class Choice(text: String, right: Boolean)

trait TestRepository {
  def ++=(ts: List[Test]): Unit
  def all: List[Test]
}


import scala.slick.driver.JdbcProfile

class SlickTestRepository(val profile: JdbcProfile) {

  import scala.slick.model.ForeignKeyAction._
  import profile.simple._

  private object schema {
    case class TestRec(id: Int, subject: String, question: String, explanation: String)
    case class ChoiceRec(testId: Int, choice: Choice)

    class Tests(t: Tag) extends Table[TestRec](t, "test") {
      val id          = column[Int]   ("id")
      val subject     = column[String]("subject")
      val question    = column[String]("question")
      val explanation = column[String]("explanation")

      def * = (id, subject, question, explanation) <> (TestRec.tupled, TestRec.unapply)
    }

    class Choices(t: Tag) extends Table[ChoiceRec](t, "choice") {
      val testId = column[Int]    ("test_id")
      val text   = column[String] ("text")
      val right  = column[Boolean]("right")

      val testFk = foreignKey("choice_test_fk", testId, Tests)(_.id, onUpdate = Restrict, onDelete = Cascade)

      val comap = ((testId: Int, text: String, right: Boolean) =>
        ChoiceRec(testId, Choice(text, right))).tupled

      val map = (r: ChoiceRec) => Option((r.testId, r.choice.text, r.choice.right))

      def * = (testId, text, right) <> (comap, map)
    }

    val Tests = TableQuery[Tests]
    val Choices = TableQuery[Choices]
  }
  import schema._

  def init()(implicit s: Session) = Seq(Tests, Choices) foreach (_.ddl.create)

  def tests(implicit s: Session) = new TestRepository {
    def ++=(ts: List[Test]): Unit = {
      ts.map { t =>
        Tests += TestRec(t.id, t.subject, t.question, t.explanation)
        Choices ++= t.choices.map(c => ChoiceRec(t.id, c))
      }
    }

    def all: List[Test] = {
      val cs = Choices.list
        .groupBy(_.testId)
        .mapValues(_.map(_.choice))
        .withDefault(Nil)
      Tests.list.map { t =>
        Test(t.id, t.subject, t.question, t.explanation, cs(t.id))
      }
    }
  }
}




