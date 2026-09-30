package com.mindae.employeesvc.eda.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.Instant;

@Entity
public class ProcessedEvent {
    @Getter
    @Id
    private String eventId;
    private Instant processedAt;

    protected ProcessedEvent() {

    }

    public ProcessedEvent(String eventId, Instant processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }
}
