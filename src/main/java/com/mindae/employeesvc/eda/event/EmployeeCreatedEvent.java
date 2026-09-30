package com.mindae.employeesvc.eda.event;

import java.time.Instant;

public record EmployeeCreatedEvent(
        String eventId,
        String eventType,
        Integer eventVersion,
        String employeeId,
        String name,
        String department,
        String email,
        Instant occurredAt
) {

}
