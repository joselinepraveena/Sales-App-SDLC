package com.sales.customer.api;

import com.sales.customer.domain.Customer;
import com.sales.customer.domain.CustomerRepository;
import com.sales.customer.events.OutboxEvent;
import com.sales.customer.events.OutboxEventRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerRepository customers;
    private final OutboxEventRepository outbox;

    public CustomerController(CustomerRepository customers, OutboxEventRepository outbox) {
        this.customers = customers;
        this.outbox = outbox;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        if (customers.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer email already exists");
        }
        Customer customer = Customer.create(request.legalName(), request.email(), request.marketingConsent());
        customers.save(customer);
        outbox.save(OutboxEvent.of(
                "com.sales.customer.created.v1",
                customer.getId().toString(),
                "{\"customerId\":\"" + customer.getId() + "\",\"status\":\"" + customer.getStatus() + "\"}"));
        return CustomerResponse.from(customer);
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable UUID id) {
        return customers.findById(id)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return customers.findAll().stream().map(CustomerResponse::from).toList();
    }

    @PutMapping("/{id}")
    @Transactional
    public CustomerResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request) {
        Customer customer = customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        customer.update(request.legalName(), request.status(), request.marketingConsent());
        customers.save(customer);
        outbox.save(OutboxEvent.of(
                "com.sales.customer.updated.v1",
                customer.getId().toString(),
                "{\"customerId\":\"" + customer.getId() + "\",\"status\":\"" + customer.getStatus() + "\"}"));
        return CustomerResponse.from(customer);
    }

    public record CreateCustomerRequest(
            @NotBlank String legalName,
            @Email @NotBlank String email,
            boolean marketingConsent) {}

    public record UpdateCustomerRequest(
            @NotBlank String legalName,
            @NotBlank String status,
            boolean marketingConsent) {}

    public record CustomerResponse(
            UUID id,
            String legalName,
            String email,
            String status,
            boolean marketingConsent) {
        static CustomerResponse from(Customer customer) {
            return new CustomerResponse(
                    customer.getId(),
                    customer.getLegalName(),
                    customer.getEmail(),
                    customer.getStatus(),
                    customer.isMarketingConsent());
        }
    }
}
