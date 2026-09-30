package com.mindae.employeesvc.service;

import com.mindae.employeesvc.eda.event.EmployeeCreatedEvent;
import com.mindae.employeesvc.eda.event.ProcessedEvent;
import com.mindae.employeesvc.eda.exception.InvalidEmployeeEventException;
import com.mindae.employeesvc.eda.repo.ProcessedEventRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EmployeeEventHandler {
    private final ProcessedEventRepo processedEventRepo;

    public EmployeeEventHandler(ProcessedEventRepo processedEventRepo) {
        this.processedEventRepo = processedEventRepo;
    }

    @Transactional
    public void handle(EmployeeCreatedEvent event) {
        if (processedEventRepo.existsById(event.eventId())) {
            System.out.println(
                    "duplicated event ignored: " + event.eventId());
            return;
        }
        // Real business processing would happen here
        System.out.println(
                "processing employee: " + event.employeeId());

        //simulate temporary problem
        if ("113".equals(event.employeeId())) {
            throw new RuntimeException("temporary processing failure");
        }

        //simulate permanent bad-data problem
        if ("111".equals(event.employeeId())) {
            throw new InvalidEmployeeEventException(
                    "Invalid employee event: " + event.employeeId()
            );
        }
        processedEventRepo.save(
                new ProcessedEvent(
                        event.eventId(),
                        Instant.now()));
    }
}