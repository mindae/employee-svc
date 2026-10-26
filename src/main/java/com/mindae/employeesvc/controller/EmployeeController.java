package com.mindae.employeesvc.controller;

import com.mindae.employeesvc.eda.entity.Employee;
import com.mindae.employeesvc.response.EmployeeResponse;
import com.mindae.employeesvc.service.DepartmentService;
import com.mindae.employeesvc.service.EmployeeEventProducer;
import com.mindae.employeesvc.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/employees")
@RestController
public class EmployeeController {

    private static final Logger log =
            LoggerFactory.getLogger(EmployeeController.class);
    private final DepartmentService deptSvc;
    private final EmployeeEventProducer eventProducer;
    private final EmployeeService empSvc;

    EmployeeController(DepartmentService deptSvc, EmployeeEventProducer eventProducer, EmployeeService empSvc) {
        this.deptSvc = deptSvc;
        this.eventProducer = eventProducer;
        this.empSvc = empSvc;
    }

    @GetMapping
    public EmployeeResponse getEmp() {
        log.info("Fetching employee with department details");
        return new EmployeeResponse(
                101L,
                "mindae",
                deptSvc.getDepartment()
        );
    }

    @GetMapping("/all")
    public List<Employee> getAllEmployees() {
        return empSvc.getAllEmployees();
    }

    @PostMapping("/{employeeId}/event")
    public String createEvent(@PathVariable String employeeId) {
        eventProducer.sendEmployeeCreatedEvent(employeeId,
                "Mindae",
                "Technology");
        return "Event published";
    }

    // Event-driven create employee endpoint
    @PostMapping("/events")
    public String createEmployee(@RequestParam String id,
                                 @RequestParam String name,
                                 @RequestParam String department) {
        empSvc.createEmpForEvent(id, name, department);
        return "Employee created";
    }

    @PostMapping
    public ResponseEntity<Employee> createCrudEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = empSvc.createEmp(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        empSvc.deleteEmp(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id,
                                                   @RequestBody Employee updatedEmployee) {
        if (!id.equals(updatedEmployee.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID must match the URL");
        }
        return ResponseEntity.ok(empSvc.updateEmp(id, updatedEmployee));
    }
}
