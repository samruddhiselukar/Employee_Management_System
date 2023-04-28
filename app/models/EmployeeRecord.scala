package models
import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.MySQLProfile.api._

case class EmployeeRecord(companyID: Int, employeeID: Int, firstName: String, lastName: String, email: String,
                          designation: String)

class EmployeeTableDef(tag: Tag) extends Table[EmployeeRecord](tag, "employee_db") {

  def companyID = column[Int]("company_id", O.PrimaryKey)
  def employeeID = column[Int]("employee_id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("name")
  def lastName = column[String]("surname")
  def email = column[String]("email", O.Unique)
  def designation = column[String]("designation")

  override def * = (companyID, employeeID, firstName, lastName, email, designation) <> (EmployeeRecord.tupled, EmployeeRecord.unapply)
}

case class EmployeeFormData(firstName: String, lastName: String, email: String, designation: String)

object EmployeeForm {
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "email" -> nonEmptyText,
      "designation" -> nonEmptyText
    )(EmployeeFormData.apply)(EmployeeFormData.unapply)
  )
}

class EmployeeList @Inject()(
                              protected val dbConfigProvider: DatabaseConfigProvider
                            )(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var employeeList = TableQuery[EmployeeTableDef]

  def add(employee: EmployeeRecord): Future[String] = {
    dbConfig.db
      .run(employeeList += employee)
      .map(res => "Employee successfully added")
      .recover {
        case ex: Exception => {
          printf(ex.getMessage())
          ex.getMessage
        }
      }
  }

  def delete(employeeID: Int): Future[Int] = {
    dbConfig.db.run(employeeList.filter(_.employeeID === employeeID).delete)
  }

  def update(employee: EmployeeRecord): Future[Int] = {
    dbConfig.db
      .run(employeeList.filter(_.employeeID === employee.employeeID)
        .map(x => (x.firstName, x.lastName, x.designation))
        .update(employee.firstName, employee.lastName, employee.designation)
      )
  }

  def get(employeeID: Int): Future[Option[EmployeeRecord]] = {
    dbConfig.db.run(employeeList.filter(_.employeeID === employeeID).result.headOption)
  }

  def listAll: Future[Seq[EmployeeRecord]] = {
    dbConfig.db.run(employeeList.result)
  }
}

