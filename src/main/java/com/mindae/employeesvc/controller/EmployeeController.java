package com.mindae.employeesvc.controller;

import com.mindae.employeesvc.service.DepartmentService;
import com.mindae.employeesvc.service.EmployeeEventProducer;
import com.mindae.employeesvc.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public String getEmp() {
        log.info("Calling department service");
        return "first emp is mindae, and the dt is: " + LocalDateTime.now()
                + " => Departments Details fetched from DeptSvc: " + departmentService.getDepartment();
    }

    @PostMapping("/{employeeId}/event")
    public String createEvent(@PathVariable String employeeId) {
        eep.sendEmployeeCreatedEvent(employeeId,
                "Mindae",
                "Technology");
        return "Event published";
    }

    //let's add CRUD operations for emp
    @PostMapping
    public String createEmployee(@RequestParam String id,
                                 @RequestParam String name,
                                 @RequestParam String department) {
        es.createEmp(id, name, department);
        return "Employee created";
    }
}
