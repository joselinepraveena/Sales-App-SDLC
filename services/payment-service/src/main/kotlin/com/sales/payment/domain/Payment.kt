package com.sales.payment.domain

import java.math.BigDecimal
import java.util.UUID

enum class PaymentStatus { AUTHORIZED, CAPTURED, FAILED, REFUNDED }

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val orderId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val providerReference: String = "tok_${UUID.randomUUID().toString().take(8)}",
    var status: PaymentStatus = PaymentStatus.AUTHORIZED,
    val idempotencyKey: String
)

class PaymentLedger {
    private val payments = mutableMapOf<UUID, Payment>()
    val events = mutableListOf<Map<String, Any>>()

    fun authorize(orderId: UUID, amount: BigDecimal, currency: String, idempotencyKey: String, fail: Boolean = false): Payment {
        payments.values.find { it.idempotencyKey == idempotencyKey }?.let { return it }
        if (fail) {
            val failed = Payment(orderId = orderId, amount = amount, currency = currency, idempotencyKey = idempotencyKey, status = PaymentStatus.FAILED)
            payments[failed.id] = failed
            events += mapOf("type" to "com.sales.payment.failed.v1", "paymentId" to failed.id.toString(), "orderId" to orderId.toString(), "status" to failed.status.name)
            return failed
        }
        val payment = Payment(orderId = orderId, amount = amount, currency = currency, idempotencyKey = idempotencyKey)
        payments[payment.id] = payment
        events += mapOf("type" to "com.sales.payment.authorized.v1", "paymentId" to payment.id.toString(), "orderId" to orderId.toString(), "status" to payment.status.name, "amount" to amount, "providerReference" to payment.providerReference)
        return payment
    }

    fun capture(id: UUID): Payment {
        val payment = payments[id] ?: error("payment not found")
        require(payment.status == PaymentStatus.AUTHORIZED)
        payment.status = PaymentStatus.CAPTURED
        return payment
    }

    fun refund(id: UUID): Payment {
        val payment = payments[id] ?: error("payment not found")
        payment.status = PaymentStatus.REFUNDED
        events += mapOf("type" to "com.sales.payment.refund-completed.v1", "paymentId" to payment.id.toString(), "orderId" to payment.orderId.toString(), "status" to payment.status.name)
        return payment
    }

    fun get(id: UUID): Payment = payments[id] ?: error("payment not found")
}
