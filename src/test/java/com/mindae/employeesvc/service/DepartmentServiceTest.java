package com.mindae.employeesvc.service;

import com.mindae.employeesvc.response.DepartmentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void shouldReturnDepartmentDetails() {

        DepartmentResponse expectedDepartmentResponse =
                new DepartmentResponse(100L, "Finance");
        when(restTemplate.getForEntity("http://department-svc/api/v1/departments",
                DepartmentResponse.class))
                .thenReturn(ResponseEntity.ok(expectedDepartmentResponse));
        DepartmentResponse actualDepartmentResponse = departmentService.getDepartment();

        assertEquals(expectedDepartmentResponse, actualDepartmentResponse);

    }

    @Test
    void fallbacksKeepTheirExistingResponses() {
        assertEquals(new DepartmentResponse(0L, "circuitbreaker: dept temp unavail"),
                departmentService.departmentFallback(new RuntimeException("department unavailable")));
        assertEquals(new DepartmentResponse(0L, "retry: dept temp unavail"),
                departmentService.retryFallbackDepartmentService(
                        new RuntimeException("department unavailable")));
    }
//
//    @Test
//    void shouldThrowExceptionWhenDepartmentFails() {
//        when(restTemplate.getForEntity("http://department-svc/api/v1/departments",
//                DepartmentResponse.class))
//                .;
//
//    }
}