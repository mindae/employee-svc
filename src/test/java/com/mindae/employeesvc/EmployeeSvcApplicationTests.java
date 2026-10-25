package com.mindae.employeesvc;

import com.mindae.employeesvc.eda.repo.OutboxEventRepository;
import com.mindae.employeesvc.response.DepartmentResponse;
import com.mindae.employeesvc.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:employee-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.kafka.listener.auto-startup=false",
        "eureka.client.enabled=false"
})
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
class EmployeeSvcApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private DepartmentService departmentService;

    @Test
    void shouldKeepExistingEmployeeResponse() throws Exception {
        when(departmentService.getDepartment())
                .thenReturn(new DepartmentResponse(100L, "Finance"));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("mindae"))
                .andExpect(jsonPath("$.department.id").value(100))
                .andExpect(jsonPath("$.department.name").value("Finance"));
    }

    @Test
    void shouldReturnSeededEmployeesFromCollectionEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/employees/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value("101"))
                .andExpect(jsonPath("$[0].department").value("Finance"))
                .andExpect(jsonPath("$[1].department").value("Engineering"))
                .andExpect(jsonPath("$[2].department").value("Human Resources"));
    }

    @Test
    void shouldInitializeSamplesIdempotentlyWithoutCreatingOutboxEvents() {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                    new ClassPathResource("data-local.sql"));
            populator.populate(connection);
            populator.populate(connection);
            return null;
        });

        assertEquals(4, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employee", Integer.class));
        assertEquals(0, outboxEventRepository.count());
    }
}
