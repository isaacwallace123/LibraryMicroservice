package com.isaacwallace.employee_service.Presentation;

import com.isaacwallace.employee_service.DataAccess.EmployeeRepository;
import com.isaacwallace.employee_service.DataAccess.EmployeeTitle;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeResponseModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final String SERVICE_URI = "/api/v1/employees";

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_ID = "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5";

    /*--> Header Tests <--*/

    @Test
    void whenUnsupportedMediaTypeRequested_thenReturnNotAcceptable() {
        this.webTestClient.get()
                .uri(SERVICE_URI)
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    /*--> Validation Tests <--*/

    @Test
    void whenBulkEmployeesAreCreated_thenAllAreSavedCorrectly() {
        this.employeeRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            EmployeeRequestModel model = EmployeeRequestModel.builder()
                    .firstName("First" + i)
                    .lastName("Last" + i)
                    .email("email" + i + "@email.com")
                    .salary(1000.00)
                    .dob(LocalDate.of(2000 + i, 1, 1))
                    .title(EmployeeTitle.ADMINISTRATOR)
                    .build();

            this.webTestClient.post()
                    .uri(SERVICE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(model)
                    .exchange()
                    .expectStatus().isCreated();
        }

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeResponseModel.class)
                .value(list -> assertTrue(list.size() >= 10));
    }

    @Test
    void getAllEmployees_emptyDB_returnEmptyList() {
        this.employeeRepository.deleteAll();

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }
}