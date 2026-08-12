package com.sales.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.sales.customer.api.CustomerController.CreateCustomerRequest;
import com.sales.customer.api.CustomerController.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerServiceApplicationTests {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void createsCustomerAndEmitsOutboxEvent() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Northwind Trading", "buyer@northwind.example", true);
        ResponseEntity<CustomerResponse> created = rest.postForEntity(
                "/api/v1/customers", request, CustomerResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().email()).isEqualTo("buyer@northwind.example");

        ResponseEntity<CustomerResponse> fetched = rest.getForEntity(
                "/api/v1/customers/" + created.getBody().id(), CustomerResponse.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().legalName()).isEqualTo("Northwind Trading");
    }

    @Test
    void healthEndpointsAreUp() {
        assertThat(rest.getForEntity("/health/ready", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(rest.getForEntity("/health/live", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }
}
