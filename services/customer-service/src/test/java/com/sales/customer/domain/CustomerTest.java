package com.sales.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerTest {
    @Test
    void newCustomersStartActive() {
        Customer customer = Customer.create("Contoso", "ops@contoso.example", false);
        assertThat(customer.getStatus()).isEqualTo("ACTIVE");
        assertThat(customer.getEmail()).isEqualTo("ops@contoso.example");
        assertThat(customer.isMarketingConsent()).isFalse();
    }
}
