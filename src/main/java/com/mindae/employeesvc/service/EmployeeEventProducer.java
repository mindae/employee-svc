package com.mindae.employeesvc.service;

import com.mindae.employeesvc.eda.event.EmployeeCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class EmployeeEventProducer {
    private final KafkaTemplate<String, EmployeeCreatedEvent> kafkaTemplate;

    public EmployeeEventProducer(KafkaTemplate<String, EmployeeCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmployeeCreatedEvent(String employeeId,
                                         String name,
                                         String department) {
        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                UUID.randomUUID().toString(),
                "EmployeeCreated",
                2,
                employeeId, name, department, null, Instant.now());
        kafkaTemplate.send("employee-events-3p", employeeId, event);
    }
}
