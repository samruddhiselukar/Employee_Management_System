package services
import com.google.inject.Inject
import models.{EmployeeFormData, EmployeeList, EmployeeRecord}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployeeService @Inject() (employees: EmployeeList) {

  def addEmployee(employee: EmployeeFormData, companyID: Int): Future[String] = {
    val record = EmployeeRecord(companyID, 0, employee.firstName, employee.lastName, employee.email, employee.designation)
    employees.add(record)
  }

  def deleteEmployee(employeeID: Int, companyID: Int): Future[Int] = {
    employees.get(employeeID).flatMap {
      case Some(employee) if employee.companyID == companyID =>
        employees.delete(employeeID)
      case _ =>
        Future.successful(0)
    }
  }

  def updateEmployee(employeeID: Int, employee: EmployeeFormData, companyID: Int): Future[Int] = {
    employees.get(employeeID).flatMap {
      case Some(record) if record.companyID == companyID =>
        val updatedRecord = record.copy(firstName = employee.firstName, lastName = employee.lastName, email = employee.email, designation = employee.designation)
        employees.update(updatedRecord)
      case _ =>
        Future.successful(0)
    }
  }

  def getEmployee(employeeID: Int, companyID: Int): Future[Option[EmployeeRecord]] = {
    employees.get(employeeID).flatMap {
      case Some(record) if record.companyID == companyID =>
        Future.successful(Some(record))
      case _ =>
        Future.successful(None)
    }
  }

  def listAllEmployees(companyID: Int): Future[Seq[EmployeeRecord]] = {
    employees.listAll.map(_.filter(_.companyID == companyID))
  }
}
