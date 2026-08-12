package controllers

import models.Forecast
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.AnalyticsService

import javax.inject.{Inject, Singleton}

@Singleton
class AnalyticsController @Inject() (cc: ControllerComponents, analytics: AnalyticsService)
    extends AbstractController(cc) {

  def kpis: Action[AnyContent] = Action(Ok(Json.toJson(analytics.kpis)))

  def forecasts: Action[AnyContent] = Action(Ok(Json.toJson(analytics.forecasts)))

  def updateForecast: Action[AnyContent] = Action { request =>
    request.body.asJson
      .map(_.validate[Forecast].fold(errors => BadRequest(JsError.toJson(errors)), forecast => Created(Json.toJson(analytics.updateForecast(forecast)))))
      .getOrElse(BadRequest(Json.obj("error" -> "JSON body required")))
  }
}
