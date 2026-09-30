package com.mindae.employeesvc.eda.exception;

public class InvalidEmployeeEventException extends RuntimeException {
    public InvalidEmployeeEventException(String msg) {
        super(msg);
    }
}
