package services

import models.Forecast
import org.scalatestplus.play.PlaySpec

class AnalyticsServiceSpec extends PlaySpec {
  "AnalyticsService" should {
    "publish ForecastUpdated when a forecast is stored" in {
      val service = new AnalyticsService()
      service.updateForecast(Forecast("2026-Q3", BigDecimal(250000), "USD"))
      service.events.head.`type` mustBe "com.sales.analytics.forecast-updated.v1"
      service.forecasts.head.period mustBe "2026-Q3"
    }
  }
}
