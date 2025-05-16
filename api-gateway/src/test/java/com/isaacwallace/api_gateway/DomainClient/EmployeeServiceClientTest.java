package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import com.isaacwallace.api_gateway.Services.Employee.DataAccess.Title;
import com.isaacwallace.api_gateway.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.api_gateway.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.api_gateway.Utils.Exceptions.NotFoundException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceClientTest {
    private final String BASE_URI = "http://localhost:8080/api/v1/employees";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private EmployeeServiceClient client;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.client = new EmployeeServiceClient(restTemplate, new ObjectMapper(), "localhost", "8080");
    }

    @Test
    void testGetEmployeeById() throws Exception {
        String id = UUID.randomUUID().toString();
        EmployeeResponseModel mockResponse = EmployeeResponseModel.builder()
                .employeeid(id)
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        EmployeeResponseModel response = client.getEmployeeByEmployeeId(id);
        assertEquals(id, response.getEmployeeid());
    }

    @Test
    void testGetAllEmployees() throws Exception {
        EmployeeResponseModel employee = EmployeeResponseModel.builder()
                .employeeid(UUID.randomUUID().toString())
                .firstName("Isaac")
                .lastName("Wallace")
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(new EmployeeResponseModel[]{employee}),
                        MediaType.APPLICATION_JSON
                ));

        assertEquals(1, client.getEmployees().size());
    }

    @Test
    void testAddEmployee() {
        EmployeeRequestModel request = EmployeeRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess());

        client.addEmployee(request);
    }

    @Test
    void testUpdateEmployee() throws Exception {
        String id = UUID.randomUUID().toString();
        EmployeeRequestModel request = EmployeeRequestModel.builder().build();

        EmployeeResponseModel responseModel = EmployeeResponseModel.builder()
                .employeeid(id)
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(responseModel),
                        MediaType.APPLICATION_JSON
                ));

        EmployeeResponseModel response = client.updateEmployee(id, request);
        assertEquals(id, response.getEmployeeid());
    }

    @Test
    void testDeleteEmployee() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        client.deleteEmployee(id);
    }

    @Test
    void testAddEmployee_InvalidInput() {
        EmployeeRequestModel invalidRequest = EmployeeRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.addEmployee(invalidRequest));
    }

    @Test
    void testAddEmployee_Duplicate() {
        EmployeeRequestModel duplicate = EmployeeRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.addEmployee(duplicate));
    }

    @Test
    void testGetEmployeeById_NotFound() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getEmployeeByEmployeeId(id));
    }
}
