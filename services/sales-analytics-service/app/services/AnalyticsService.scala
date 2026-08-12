package services

import models.{DomainEvent, Forecast, KpiSnapshot}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable.ListBuffer

@Singleton
class AnalyticsService @Inject() () {
  private val forecastStore = ListBuffer.empty[Forecast]
  val events: ListBuffer[DomainEvent] = ListBuffer.empty

  def kpis: KpiSnapshot = KpiSnapshot(ordersConfirmed = 12, paymentSuccessRate = 0.97, pipelineAmount = BigDecimal(184000), currency = "USD")

  def updateForecast(forecast: Forecast): Forecast = {
    forecastStore += forecast
    events += DomainEvent("com.sales.analytics.forecast-updated.v1", forecast.period, forecast.forecastAmount, forecast.currency)
    forecast
  }

  def forecasts: Seq[Forecast] = forecastStore.toSeq
}
