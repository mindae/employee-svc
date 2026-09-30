package com.mindae.employeesvc.eda.repo;

import com.mindae.employeesvc.eda.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
