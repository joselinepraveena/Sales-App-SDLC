package com.sales.payment.domain

import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class PaymentLedgerTest {
    @Test
    fun authorizeIsIdempotent() {
        val ledger = PaymentLedger()
        val order = UUID.randomUUID()
        val first = ledger.authorize(order, BigDecimal("10.00"), "USD", "key-1")
        val second = ledger.authorize(order, BigDecimal("10.00"), "USD", "key-1")
        assertEquals(first.id, second.id)
        assertEquals("com.sales.payment.authorized.v1", ledger.events.first()["type"])
    }

    @Test
    fun refundEmitsCompletedEvent() {
        val ledger = PaymentLedger()
        val payment = ledger.authorize(UUID.randomUUID(), BigDecimal("25.00"), "USD", "key-2")
        ledger.refund(payment.id)
        assertEquals("REFUNDED", ledger.get(payment.id).status.name)
        assertEquals("com.sales.payment.refund-completed.v1", ledger.events.last()["type"])
    }
}
