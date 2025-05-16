package com.isaacwallace.transaction_service.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isaacwallace.transaction_service.DomainClient.Employee.EmployeeServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Employee.Models.EmployeeResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Employee.Models.Title;
import com.isaacwallace.transaction_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.transaction_service.Utils.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceClientTest {
    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_EMPLOYEE_ID = "61d2d9f8-e144-4984-8bcb-7fa29ef4fdf6";

    private static final String BASE_URL = "http://localhost:8080/api/v1/employees";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private EmployeeServiceClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        this.server = MockRestServiceServer.createServer(restTemplate);
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.client = new EmployeeServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void testEmployeeResponseModel() {
        EmployeeResponseModel employee = EmployeeResponseModel.builder()
                .employeeid("emp-123")
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("isaac@me.com")
                .dob(LocalDate.of(2000, 1, 1))
                .age(30)
                .salary(1200.0)
                .build();

        assertEquals("emp-123", employee.getEmployeeid());
        assertEquals("Isaac", employee.getFirstName());
        assertEquals(30, employee.getAge());
    }

    @Test
    void testEmployeeResponseModelEqualsHashCode() {
        EmployeeResponseModel employee1 = EmployeeResponseModel.builder()
                .employeeid("emp-123")
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("isaac@me.com")
                .dob(LocalDate.of(2000, 1, 1))
                .age(30)
                .salary(1200.0)
                .build();

        EmployeeResponseModel employee2 = EmployeeResponseModel.builder()
                .employeeid("emp-123")
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("isaac@me.com")
                .dob(LocalDate.of(2000, 1, 1))
                .age(30)
                .salary(1200.0)
                .build();

        assertEquals(employee1, employee2);
        assertEquals(employee1.hashCode(), employee2.hashCode());
    }

    @Test
    void testGetEmployeeById_fromServiceClient() throws Exception {
        EmployeeResponseModel mockEmployee = EmployeeResponseModel.builder()
                .employeeid(VALID_EMPLOYEE_ID)
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("isaac@me.com")
                .dob(LocalDate.of(2000, 1, 1))
                .age(30)
                .salary(1200.0)
                .build();

        String json = objectMapper.writeValueAsString(mockEmployee);

        server.expect(requestTo(BASE_URL + "/" + VALID_EMPLOYEE_ID))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        EmployeeResponseModel result = client.getEmployeeById(VALID_EMPLOYEE_ID);

        assertNotNull(result);
        assertEquals("Isaac", result.getFirstName());
        assertEquals(VALID_EMPLOYEE_ID, result.getEmployeeid());

        server.verify();
    }

    @Test
    void whenEmployeeNotFound_thenThrowNotFoundException() {
        server.expect(requestTo(BASE_URL + "/" + NOT_FOUND_ID))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getEmployeeById(NOT_FOUND_ID));
    }

    @Test
    void whenEmployeeIdIsInvalid_thenThrowInvalidInputException() {
        server.expect(requestTo(BASE_URL + "/" + INVALID_ID))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.getEmployeeById(INVALID_ID));
    }
}
