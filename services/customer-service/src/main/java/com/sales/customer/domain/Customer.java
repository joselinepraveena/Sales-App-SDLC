package com.sales.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String status;

    private String creditProfileRef;
    private boolean marketingConsent;
    private Instant createdAt;
    private Instant updatedAt;

    public static Customer create(String legalName, String email, boolean marketingConsent) {
        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.legalName = legalName;
        customer.email = email;
        customer.status = "ACTIVE";
        customer.marketingConsent = marketingConsent;
        customer.createdAt = Instant.now();
        customer.updatedAt = customer.createdAt;
        return customer;
    }

    public void update(String legalName, String status, boolean marketingConsent) {
        this.legalName = legalName;
        this.status = status;
        this.marketingConsent = marketingConsent;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getLegalName() { return legalName; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getCreditProfileRef() { return creditProfileRef; }
    public boolean isMarketingConsent() { return marketingConsent; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
