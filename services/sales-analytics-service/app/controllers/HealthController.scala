package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}

@Singleton
class HealthController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {
  def live: Action[AnyContent] = Action(Ok(Json.obj("status" -> "UP")))
  def ready: Action[AnyContent] = Action(Ok(Json.obj("status" -> "UP")))
  def startup: Action[AnyContent] = Action(Ok(Json.obj("status" -> "UP")))
}
