package models

import play.api.libs.json.{Json, OFormat}

final case class KpiSnapshot(ordersConfirmed: Int, paymentSuccessRate: Double, pipelineAmount: BigDecimal, currency: String)
object KpiSnapshot { implicit val format: OFormat[KpiSnapshot] = Json.format[KpiSnapshot] }

final case class Forecast(period: String, forecastAmount: BigDecimal, currency: String)
object Forecast { implicit val format: OFormat[Forecast] = Json.format[Forecast] }

final case class DomainEvent(`type`: String, period: String, forecastAmount: BigDecimal, currency: String)
object DomainEvent { implicit val format: OFormat[DomainEvent] = Json.format[DomainEvent] }
