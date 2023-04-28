package controllers.apis

import scala.concurrent.duration._
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{EmployeeFormData, EmployeeList, EmployeeRecord}
import services.EmployeeService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class EmployeeDataController @Inject()(employeeList: EmployeeList, ec: ExecutionContext,
                                    cc: ControllerComponents,
                                    employeeService: EmployeeService
                                  ) extends AbstractController(cc) {

  implicit val employeeRecordFormat: OFormat[EmployeeRecord] = Json.format[EmployeeRecord]
  implicit val employeeFormDataReads: Reads[EmployeeFormData] = Json.reads[EmployeeFormData]

  def listAllEmployees(companyID: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    employeeService.listAllEmployees(companyID) map { employees =>
      Ok(Json.toJson(employees))
    }
  }

  def getEmployee(employeeID: Int, companyID: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    employeeService.getEmployee(employeeID, companyID) map {
      case Some(employee) => Ok(Json.toJson(employee))
      case None => NotFound
    }
  }

  def addEmployee(companyID: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asJson.flatMap(_.asOpt[EmployeeFormData]).map { employeeFormData =>
      employeeService.addEmployee(employeeFormData, companyID).map( _ => Ok)
    }.getOrElse(Future.successful(BadRequest("Invalid JSON")))
  }

  def updateEmployee(employeeID: Int, companyID: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asJson.flatMap(_.asOpt[EmployeeFormData]).map { employeeFormData =>
      employeeService.updateEmployee(employeeID, employeeFormData, companyID).map( _ => Ok)
    }.getOrElse(Future.successful(BadRequest("Invalid JSON")))
  }

  def deleteEmployee(employeeID: Int, companyID: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    employeeService.deleteEmployee(employeeID, companyID) map { rowsAffected =>
      if (rowsAffected > 0) {
        Ok
      } else {
        NotFound
      }
    }
  }
}
