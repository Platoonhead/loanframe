package controllers

import javax.inject.{Inject, Singleton}
import play.api.http.HttpErrorHandler
import play.api.i18n._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(implicit val messagesApi: MessagesApi) extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {

    implicit val messageApi: Messages = MessagesImpl(messagesApi.preferred(request).lang, messagesApi)

    Future.successful(NotFound(views.html.error.error404()))
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {

    implicit val messageApi: Messages = MessagesImpl(messagesApi.preferred(request).lang, messagesApi)

    Future.successful(InternalServerError(views.html.error.error500()))
  }
}
