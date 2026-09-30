package com.mindae.employeesvc.service;

import com.mindae.employeesvc.eda.entity.Employee;
import com.mindae.employeesvc.eda.event.EmployeeCreatedEvent;
import com.mindae.employeesvc.eda.event.OutboxEvent;
import com.mindae.employeesvc.eda.repo.EmployeeRepository;
import com.mindae.employeesvc.eda.repo.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository empRepo;
    private final OutboxEventRepository outBoxRepo;
    private final ObjectMapper objectMapper;

    public EmployeeService(
            EmployeeRepository er,
            OutboxEventRepository oer,
            ObjectMapper om) {

        this.empRepo = er;
        this.outBoxRepo = oer;
        this.objectMapper = om;
    }

    @Transactional
    public void createEmp(
            String employeeId,
            String name,
            String department) {

        Employee employee =
                new Employee(employeeId, name, department);

        empRepo.save(employee);

        EmployeeCreatedEvent event =
                new EmployeeCreatedEvent(
                        UUID.randomUUID().toString(),
                        "EmployeeCreated",
                        2,
                        employeeId,
                        name,
                        department,
                        null,
                        Instant.now()
                );

        String payload =
                objectMapper.writeValueAsString(event);

        OutboxEvent oEvent =
                new OutboxEvent(
                        event.eventId(),
                        employeeId,
                        event.eventType(),
                        payload,
                        event.occurredAt()
                );

        outBoxRepo.save(oEvent);
    }
}