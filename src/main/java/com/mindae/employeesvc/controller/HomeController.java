package com.mindae.employeesvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "Welcome to EMP home location: "+ LocalDateTime.now();
    }


}
