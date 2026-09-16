package com.mindae.employeesvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mindae.employeesvc.service.DepartmentService;
import java.time.LocalDateTime;

@RequestMapping("/api/v1")
@RestController
public class EmployeeController {

    private final DepartmentService departmentService;
    private static final Logger log =
            LoggerFactory.getLogger(EmployeeController.class);

    EmployeeController(DepartmentService ds) {
        this.departmentService=ds;
    }

    @GetMapping("/employees")
    public String getEmp() {
        log.info("Calling department service");
        return "first emp is mindae, and the dt is: "+ LocalDateTime.now()
                +" => Departments Details fetched from DeptSvc: "+departmentService.getDepartment();
    }

    //let's add CRUD operations for emp
}
