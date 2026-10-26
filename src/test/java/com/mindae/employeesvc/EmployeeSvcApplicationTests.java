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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void shouldCreateEmployeeAndRejectDuplicateId() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content("""
                                {"id":"new-employee","name":"New Employee","department":"Engineering"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("new-employee"))
                .andExpect(jsonPath("$.name").value("New Employee"))
                .andExpect(jsonPath("$.department").value("Engineering"));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content("""
                                {"id":"101","name":"Duplicate","department":"Engineering"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateEmployeeAndRejectMissingOrMismatchedIds() throws Exception {
        mockMvc.perform(put("/api/v1/employees/101")
                        .contentType("application/json")
                        .content("""
                                {"id":"101","name":"Updated Employee","department":"Operations"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Employee"))
                .andExpect(jsonPath("$.department").value("Operations"));

        mockMvc.perform(put("/api/v1/employees/missing")
                        .contentType("application/json")
                        .content("""
                                {"id":"missing","name":"Unknown","department":"Operations"}
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/v1/employees/101")
                        .contentType("application/json")
                        .content("""
                                {"id":"different","name":"Mismatch","department":"Operations"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteEmployeeAndReturnNotFoundWhenMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/101"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/employees/missing"))
                .andExpect(status().isNotFound());
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
