package com.sales.payment

import com.sales.payment.domain.PaymentLedger
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val ledger = PaymentLedger()
    install(ContentNegotiation) { json() }
    routing {
        get("/health/live") { call.respond(mapOf("status" to "UP")) }
        get("/health/ready") { call.respond(mapOf("status" to "UP")) }
        get("/health/startup") { call.respond(mapOf("status" to "UP")) }

        post("/api/v1/payments/authorize") {
            val req = call.receive<AuthorizeRequest>()
            val payment = ledger.authorize(
                orderId = UUID.fromString(req.orderId),
                amount = BigDecimal(req.amount),
                currency = req.currency,
                idempotencyKey = req.idempotencyKey,
                fail = req.simulateFailure
            )
            val status = if (payment.status.name == "FAILED") HttpStatusCode.UnprocessableEntity else HttpStatusCode.Created
            call.respond(status, payment.toResponse())
        }

        post("/api/v1/payments/{id}/capture") {
            val payment = ledger.capture(UUID.fromString(call.parameters["id"]))
            call.respond(payment.toResponse())
        }

        post("/api/v1/payments/{id}/refund") {
            val payment = ledger.refund(UUID.fromString(call.parameters["id"]))
            call.respond(payment.toResponse())
        }

        get("/api/v1/payments/{id}") {
            call.respond(ledger.get(UUID.fromString(call.parameters["id"])).toResponse())
        }
    }
}

@Serializable
data class AuthorizeRequest(
    val orderId: String,
    val amount: String,
    val currency: String,
    val idempotencyKey: String,
    val simulateFailure: Boolean = false
)

@Serializable
data class PaymentResponse(
    val paymentId: String,
    val orderId: String,
    val status: String,
    val amount: String,
    val currency: String,
    val providerReference: String
)

private fun com.sales.payment.domain.Payment.toResponse() = PaymentResponse(
    paymentId = id.toString(),
    orderId = orderId.toString(),
    status = status.name,
    amount = amount.toPlainString(),
    currency = currency,
    providerReference = providerReference
)
