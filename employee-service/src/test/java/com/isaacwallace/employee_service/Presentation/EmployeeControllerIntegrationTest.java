package com.isaacwallace.employee_service.Presentation;

import com.isaacwallace.employee_service.DataAccess.Employee;
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

import java.sql.Timestamp;
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

    /*--> RequestModel Tests <--*/

    @Test
    void testEqualsAndHashCodeOnRequestModel() {
        EmployeeRequestModel model1 = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(LocalDate.of(1995, 10, 5))
                .email("isaac@example.com")
                .title(EmployeeTitle.ADMINISTRATOR)
                .salary(90000.0)
                .build();

        EmployeeRequestModel model2 = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(LocalDate.of(1995, 10, 5))
                .email("isaac@example.com")
                .title(EmployeeTitle.ADMINISTRATOR)
                .salary(90000.0)
                .build();

        EmployeeRequestModel modelDifferent = EmployeeRequestModel.builder()
                .firstName("NotIsaac")
                .lastName("Wallace")
                .dob(LocalDate.of(1990, 1, 1))
                .email("different@example.com")
                .title(EmployeeTitle.MANAGER)
                .salary(100000.0)
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
        assertNotEquals(model1, modelDifferent);
        assertEquals(model1, model1); // same reference
    }

    @Test
    void testGettersOnRequestModel() {
        LocalDate dob = LocalDate.of(1995, 10, 5);
        EmployeeRequestModel model = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(dob)
                .email("isaac@example.com")
                .title(EmployeeTitle.ADMINISTRATOR)
                .salary(90000.0)
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals(dob, model.getDob());
        assertEquals("isaac@example.com", model.getEmail());
        assertEquals(EmployeeTitle.ADMINISTRATOR, model.getTitle());
        assertEquals(90000.0, model.getSalary());
    }


    @Test
    void testRequestModelConstructor() {
        EmployeeRequestModel model = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), model.getDob());
        assertEquals("IsaacWallace@me.com", model.getEmail());
        assertEquals(1000.00, model.getSalary());
        assertEquals(EmployeeTitle.ADMINISTRATOR, model.getTitle());
    }

    @Test
    void testRequestModelEqualsAndHashCode() {
        EmployeeRequestModel model = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        EmployeeRequestModel model2 = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        assertEquals(model, model2);
        assertEquals(model.hashCode(), model2.hashCode());
    }

    @Test
    void testRequestModelToString() {
        EmployeeRequestModel model = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        assertTrue(model.toString().contains("Isaac"));
        assertTrue(model.toString().contains("Wallace"));
        assertTrue(model.toString().contains("IsaacWallace@me.com"));
        assertTrue(model.toString().contains("ADMINISTRATOR"));
    }

    /*--> ResponseModel Tests <--*/

    @Test
    void testSetEmployeeidOnResponseModel() {
        EmployeeResponseModel model = EmployeeResponseModel.builder().build();
        model.setEmployeeid("E123");
        assertEquals("E123", model.getEmployeeid());
    }

    @Test
    void testSetAgeOnResponseModel() {
        EmployeeResponseModel model = EmployeeResponseModel.builder().build();
        model.setAge(30);
        assertEquals(30, model.getAge());
    }

    @Test
    void testEqualsAndHashCode() {
        EmployeeResponseModel model1 = EmployeeResponseModel.builder()
                .employeeid("E001")
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dob(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .title(EmployeeTitle.MANAGER)
                .salary(85000.0)
                .build();

        EmployeeResponseModel model2 = EmployeeResponseModel.builder()
                .employeeid("E001")
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dob(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .title(EmployeeTitle.MANAGER)
                .salary(85000.0)
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
        assertEquals(model1, model1); // identity
    }

    @Test
    void testResponseModelContructor() {
        EmployeeResponseModel model = EmployeeResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .age(30)
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), model.getDob());
        assertEquals("IsaacWallace@me.com", model.getEmail());
        assertEquals(1000.00, model.getSalary());
        assertEquals(EmployeeTitle.ADMINISTRATOR, model.getTitle());
    }

    @Test
    void testResponseModelSettersAndGetters() {
        EmployeeResponseModel model = EmployeeResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .age(30)
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        model.setFirstName("Test");
        model.setLastName("Boob");
        model.setDob(LocalDate.of(2001, 1, 1));
        model.setEmail("Different");
        model.setSalary(2000.00);
        model.setTitle(EmployeeTitle.MANAGER);

        assertEquals("Test", model.getFirstName());
        assertEquals("Boob", model.getLastName());
        assertEquals(LocalDate.of(2001, 1, 1), model.getDob());
        assertEquals("Different", model.getEmail());
        assertEquals(2000.00, model.getSalary());
        assertEquals(EmployeeTitle.MANAGER, model.getTitle());
    }

    @Test
    void testResponseModelEqualsAndHashCode() {
        EmployeeResponseModel model1 = EmployeeResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .age(30)
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        EmployeeResponseModel model2 = EmployeeResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .age(30)
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testResponseModelToString() {
        EmployeeResponseModel model = EmployeeResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .age(30)
                .dob(LocalDate.of(2000, 1, 1))
                .email("IsaacWallace@me.com")
                .salary(1000.00)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        assertTrue(model.toString().contains("Isaac"));
        assertTrue(model.toString().contains("Wallace"));
        assertTrue(model.toString().contains("IsaacWallace@me.com"));
        assertTrue(model.toString().contains("ADMINISTRATOR"));
    }


    /*--> CRUD Tests <--*/

    @Test
    void whenEmployeeExists_thenReturnAllEmployees() {
        long sizeDB = this.employeeRepository.count();

        this.webTestClient.get()
            .uri(SERVICE_URI)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(EmployeeResponseModel.class)
            .value((list) -> {
                assertNotNull(list);
                assertNotEquals(0, sizeDB);
                assertEquals(sizeDB, list.size());

                list.forEach((employeeResponseModel) -> {
                    assertNotNull(employeeResponseModel);
                    assertNotNull(employeeResponseModel.getEmployeeid());
                    assertNotNull(employeeResponseModel.getFirstName());
                    assertNotNull(employeeResponseModel.getLastName());
                    assertNotNull(employeeResponseModel.getEmail());
//                    assertTrue(employeeResponseModel.getAge() > 0);
                    assertNotNull(employeeResponseModel.getSalary());
                    assertNotNull(employeeResponseModel.getDob());
                    assertNotNull(employeeResponseModel.getTitle());
                });
            });
    }

    @Test
    void whenEmployeeIdExists_thenReturnIsOk() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(EmployeeResponseModel.class)
            .value((employeeResponseModel) -> {
                assertNotNull(employeeResponseModel);
                assertNotNull(employeeResponseModel.getEmployeeid());
                assertNotNull(employeeResponseModel.getFirstName());
                assertNotNull(employeeResponseModel.getLastName());
                assertNotNull(employeeResponseModel.getEmail());
//                assertTrue(employeeResponseModel.getAge() > 0);
                assertNotNull(employeeResponseModel.getSalary());
                assertNotNull(employeeResponseModel.getDob());
                assertNotNull(employeeResponseModel.getTitle());

                assertEquals(VALID_ID, employeeResponseModel.getEmployeeid());
            });
    }

    @Test
    void whenAuthorExistsOnCreate_thenReturnIsCreated() {
        EmployeeRequestModel employeeRequestModel = EmployeeRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .email("IsaacWallace@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(employeeRequestModel)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    void whenAuthorExistsOnUpdate_thenReturnIsOk() {
        EmployeeRequestModel employeeRequestModel = EmployeeRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .email("IsaacWallace@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(employeeRequestModel)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void whenAuthorExistsOnDelete_thenReturnIsNoContent() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isNoContent();
    }

    /*--> GET Tests <--*/

    @Test
    void whenAuthorIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown employeeid: " + NOT_FOUND_ID);
    }

    @Test
    void whenAuthorIdIsInvalidOnGet_thenReturnUnprocessableEntity() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid employeeid: " +  INVALID_ID);
    }

    /*--> POST Tests <--*/

    @Test
    void whenAuthorLastNameIsNullOnPost_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
                .firstName("John")
                .lastName(null)
                .email("JohnDoe@me.com")
                .salary(1000.00)
                .dob(LocalDate.of(2000, 1, 1))
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: null");
    }

    @Test
    void whenAuthorFirstNameIsNullOnPost_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
                .firstName(null)
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .salary(1000.00)
                .dob(LocalDate.of(2000, 1, 1))
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: null");
    }

    @Test
    void whenAuthorEmailIsNullOnPost_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email(null)
                .salary(1000.00)
                .dob(LocalDate.of(2000, 1, 1))
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid email: null");
    }

    @Test
    void whenAuthorSalaryIsNullOnPost_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .salary(null)
                .dob(LocalDate.of(2000, 1, 1))
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid salary: null");
    }

    @Test
    void whenAuthorDobIsNullOnPost_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .salary(1000.00)
                .dob(null)
                .title(EmployeeTitle.ADMINISTRATOR)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid dob: null");
    }

    @Test
    void whenAuthorTitleIsNullOnPost_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .salary(1000.00)
                .dob(LocalDate.of(2000, 1, 1))
                .title(null)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid title: null");
    }

    @Test
    void whenAuthorFirstNameAndLastNameAlreadyExistOnPost_thenReturnConflict() {
        Employee newEmployee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        this.employeeRepository.save(newEmployee);

        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("TestMail@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Duplicate name: " + requestModel.getFirstName() + " " + requestModel.getLastName());
    }

    @Test
    void whenAuthorEmailAlreadyExistsOnPost_thenReturnConflict() {
        Employee newEmployee = new Employee("Johnny", "Test", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        this.employeeRepository.save(newEmployee);

        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Duplicate email: " + requestModel.getEmail());
    }

    /*--> PUT Tests <--*/

    @Test
    void whenAuthorIdDoesNotExistOnPut_thenReturnNotFound() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown employeeid: " + NOT_FOUND_ID);
    }

    @Test
    void whenAuthorIdIsInvalidOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid employeeid: " + INVALID_ID);
    }

    @Test
    void whenAuthorFirstNameIsNullOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName(null)
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: null");
    }

    @Test
    void whenAuthorLastNameIsNullOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName(null)
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: null");
    }

    @Test
    void whenAuthorEmailIsNullOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email(null)
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid email: null");
    }

    @Test
    void whenAuthorDobIsNullOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(null)
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid dob: null");
    }

    @Test
    void whenAuthorSalaryIsNullOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(null)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid salary: null");
    }

    @Test
    void whenAuthorTitleIsNullOnPut_thenReturnUnprocessableEntity() {
        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(null)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid title: null");
    }

    @Test
    void whenAuthorFirstNameAndLastNameAlreadyExistOnPut_thenReturnConflict() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        this.employeeRepository.save(employee);

        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnnyTest@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Duplicate name: " + requestModel.getFirstName() + " " + requestModel.getLastName());
    }

    @Test
    void whenAuthorEmailAlreadyExistsOnPut_thenReturnConflict() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        this.employeeRepository.save(employee);

        EmployeeRequestModel requestModel = EmployeeRequestModel.builder()
            .firstName("Johnny")
            .lastName("Test")
            .email("JohnDoe@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
            .title(EmployeeTitle.ADMINISTRATOR)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Duplicate email: " + requestModel.getEmail());
    }

    /*--> DELETE Tests <--*/

    @Test
    void whenAuthorIdIsNotFoundOnDelete_thenReturnNotFound() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown employeeid: " + NOT_FOUND_ID);
    }

    @Test
    void whenAuthorIdIsInvalidOnDelete_thenReturnUnprocessableEntity() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid employeeid: " +  INVALID_ID);
    }
}