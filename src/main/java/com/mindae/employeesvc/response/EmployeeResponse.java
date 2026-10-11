package com.mindae.employeesvc.response;

public record EmployeeResponse(
        Long id,
        String name,
        DepartmentResponse department
) {
}
