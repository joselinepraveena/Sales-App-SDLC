package com.sales.customer.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;
    private String eventType;
    private String aggregateId;
    @Column(length = 4000)
    private String payload;
    private Instant createdAt;
    private boolean published;

    public static OutboxEvent of(String eventType, String aggregateId, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.id = UUID.randomUUID();
        event.eventType = eventType;
        event.aggregateId = aggregateId;
        event.payload = payload;
        event.createdAt = Instant.now();
        event.published = false;
        return event;
    }

    public UUID getId() { return id; }
    public String getEventType() { return eventType; }
    public String getAggregateId() { return aggregateId; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isPublished() { return published; }
}
