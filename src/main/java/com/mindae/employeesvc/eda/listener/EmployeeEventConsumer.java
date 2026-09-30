package com.mindae.employeesvc.eda.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindae.employeesvc.eda.event.EmployeeCreatedEvent;
import com.mindae.employeesvc.service.EmployeeEventHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventConsumer {
    private final ObjectMapper objectMapper;
    private final EmployeeEventHandler employeeEventHandler;

    public EmployeeEventConsumer(ObjectMapper objectMapper, EmployeeEventHandler employeeEventHandler) {
        this.objectMapper = objectMapper;
        this.employeeEventHandler = employeeEventHandler;
    }

    @KafkaListener(topics = "employee-events-3p",
            groupId = "employee-processors"
    )
    public void consume(String msg) throws JsonProcessingException {
        EmployeeCreatedEvent event = objectMapper.readValue(
                msg,
                EmployeeCreatedEvent.class
        );
        int version =
                event.eventVersion() == null
                        ? 1
                        : event.eventVersion();
        System.out.println(
                "Processing event version: " + version
        );
        System.out.println("Received event: " + event);
        employeeEventHandler.handle(event);
    }
}
