package slickways

case class Test(id: Int, subject: String, question: String, explanation: String)

case class Choice(testId: Int, text: String, right: Boolean)


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

  object Tests extends TableQuery(new Tests(_)) {
    def add(tests: List[Test])(implicit session: Session) = this ++= tests
    def all(implicit session: Session): List[Test] = this.list
  }

  class Choices(t: Tag) extends Table[Choice](t, "choice") {
    val testId = column[Int]    ("test_id")
    val text   = column[String] ("text")
    val right  = column[Boolean]("right")

    val testFk = foreignKey("choice_test_fk", testId, Tests)(_.id, onUpdate = Restrict, onDelete = Cascade)

    def * = (testId, text, right) <> (Choice.tupled, Choice.unapply)
  }

  object Choices extends TableQuery(new Choices(_)) {
    def add(choices: List[Choice])(implicit session: Session) = this ++= choices
    def all(implicit session: Session): List[Choice] = this.list
  }

  def create()(implicit s: Session) = Seq(Tests, Choices) foreach (_.ddl.create)
}
