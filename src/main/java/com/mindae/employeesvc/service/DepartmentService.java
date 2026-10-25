package com.mindae.employeesvc.service;

import com.mindae.employeesvc.response.DepartmentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final RestTemplate restTemplate;
    public DepartmentService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name="departmentService", fallbackMethod="departmentFallback")
    @Retry(name="retryDepartmentService", fallbackMethod="retryFallbackDepartmentService")
    public DepartmentResponse getDepartment() {
        ResponseEntity<DepartmentResponse> resp =
                restTemplate.getForEntity("http://department-svc/api/v1/departments",
                DepartmentResponse.class);
        return resp.getBody();
    }
    //circuit
    public DepartmentResponse departmentFallback(Throwable t) {
        log.warn("Department circuit-breaker fallback invoked: cause={}", t.toString());
        return new DepartmentResponse(0L, "circuitbreaker: dept temp unavail");
    }

    public DepartmentResponse retryFallbackDepartmentService(Throwable t) {
        log.warn("Department retry fallback invoked: cause={}", t.toString());
        return new DepartmentResponse(0L, "retry: dept temp unavail");
    }

    
}
