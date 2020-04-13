package forms

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n._

case class AddCitizenResponse(country: String,
                              name: String,
                              age: String,
                              isAffected: Boolean)

case class ErrorMessage(message: String)
case class SuccessMessage(message: String)

class UserForm @Inject()(langs: Langs, val messagesApi: MessagesApi) {

  val addCitizenForm = Form(mapping(
    "country" -> text.verifying("Please provide a valid country", _.nonEmpty),
    "name" -> text.verifying("Please provide citizen name", _.nonEmpty),
    "age" -> text.verifying("please provide a valid age", age => age.nonEmpty && age.forall(_.isDigit)),
    "isAffected" -> boolean)(AddCitizenResponse.apply)(AddCitizenResponse.unapply))
}
