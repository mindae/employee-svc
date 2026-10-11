package com.mindae.employeesvc.controller;

import com.mindae.employeesvc.response.EmployeeResponse;
import com.mindae.employeesvc.service.DepartmentService;
import com.mindae.employeesvc.service.EmployeeEventProducer;
import com.mindae.employeesvc.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/employees")
@RestController
public class EmployeeController {

    private static final Logger log =
            LoggerFactory.getLogger(EmployeeController.class);
    private final DepartmentService departmentService;
    private final EmployeeEventProducer eep;
    private final EmployeeService es;

    EmployeeController(DepartmentService ds, EmployeeEventProducer eep, EmployeeService es) {
        this.departmentService = ds;
        this.eep = eep;
        this.es = es;
    }

    @GetMapping
    public EmployeeResponse getEmp() {
        log.info("Fetching employee with department details");
        return new EmployeeResponse(
                101L,
                "mindae",
                departmentService.getDepartment()
        );
    }

    @PostMapping("/{employeeId}/event")
    public String createEvent(@PathVariable String employeeId) {
        eep.sendEmployeeCreatedEvent(employeeId,
                "Mindae",
                "Technology");
        return "Event published";
    }

    @PostMapping
    public String createEmployee(@RequestParam String id,
                                 @RequestParam String name,
                                 @RequestParam String department) {
        es.createEmp(id, name, department);
        return "Employee created";
    }
}
