package controllers

import forms.{ErrorMessage, SuccessMessage, UserForm}
import javax.inject._
import models.{CitizenInfo, Covid19UsersRepository}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Application @Inject()(val controllerComponents: ControllerComponents,
                            Covid19UsersRepository: Covid19UsersRepository,
                            userForm: UserForm) extends BaseController {

  implicit val formatProfile: OFormat[ErrorMessage] = Json.format[ErrorMessage]
  implicit val formatSuccessMessageProfile: OFormat[SuccessMessage] = Json.format[SuccessMessage]
  implicit val formatCitizenInfoProfile: OFormat[CitizenInfo] = Json.format[CitizenInfo]

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def addCitizen(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    userForm.addCitizenForm.bindFromRequest.fold(
      _ => {
        Future.successful(BadRequest(Json.toJson(ErrorMessage("Malformed Request!"))))
      },
      data => {
        val citizenInfo = CitizenInfo(0, data.country, data.name, data.age.toInt, data.isAffected)
        Covid19UsersRepository.store(citizenInfo).map { isUpdated =>
          if (isUpdated) Ok(Json.toJson(SuccessMessage("citizen successfully added"))) else InternalServerError(Json.toJson(ErrorMessage("Something went wrong")))
        }
      })
  }

  def flipStatus(citizenId: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Covid19UsersRepository.getCitizenById(citizenId).flatMap { optionalCitizen =>
      optionalCitizen.fold {
        Future.successful(NotFound(Json.toJson(ErrorMessage("User not found to flip status"))))
      } { citizenInfo =>
        Covid19UsersRepository.flipStatus(citizenInfo.id, citizenInfo.isAffected).map { isFlipped =>
          if (isFlipped) Ok(Json.toJson(SuccessMessage("Successfully Flipped status"))) else InternalServerError(Json.toJson(ErrorMessage("Something went wrong")))
        }
      }
    }
  }

  def getCountries: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Covid19UsersRepository.getCountries.map { countries =>
      Ok(Json.toJson(countries.distinct))
    }
  }

  def getByCountry(country: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Covid19UsersRepository.getByCountry(country).map { countries =>
      Ok(Json.toJson(countries.distinct))
    }
  }
}
