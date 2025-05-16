package com.isaacwallace.transaction_service.Presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isaacwallace.transaction_service.DataAccess.*;
import com.isaacwallace.transaction_service.DomainClient.Employee.EmployeeServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Employee.Models.EmployeeResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Employee.Models.Title;
import com.isaacwallace.transaction_service.DomainClient.Inventory.InventoryServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Inventory.Models.BookResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Membership.MembershipServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.Address;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.MemberResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.Phone;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.PhoneType;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private MembershipServiceClient membershipServiceClient;

    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;

    @MockitoBean
    private EmployeeServiceClient employeeServiceClient;

    private MockRestServiceServer mockRestServiceServer;
    private ObjectMapper objectMapper;

    private final String BASE_URI_EMPLOYEES = "http://localhost:8080/api/v1/employees";
    private final String BASE_URI_MEMBER = "http://localhost:8080/api/v1/members";
    private final String BASE_URI_INVENTORY = "http://localhost:8080/api/v1/inventory";

    private final String BASE_URI = "/api/v1/transactions";
    private final String SERVICE_URI = "/api/v1/members/{memberid}/transactions";

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";

    private final String VALID_TRANSACTION_ID = "8b0a2c1b-734f-4cb1-b123-5f1e3e2b5a4f";

    private final String VALID_MEMBER_ID = "823e4567-e89b-12d3-a456-556642440007";
    private final String VALID_INVENTORY_ID = "c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5";
    private final String VALID_EMPLOYEE_ID = "61d2d9f8-e144-4984-8bcb-7fa29ef4fdf6";
    private final String VALID_AUTHOR_ID = "123e4567-e89b-12d3-a456-556642440000";

    @BeforeEach
    public void setup() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setupMocks() {
        Mockito.when(employeeServiceClient.getEmployeeById(VALID_EMPLOYEE_ID))
                .thenReturn(EmployeeResponseModel.builder()
                        .employeeid(VALID_EMPLOYEE_ID)
                        .firstName("Isaac")
                        .lastName("Wallace")
                        .age(30)
                        .title(Title.ADMINISTRATOR)
                        .email("IsaacWallace@me.com")
                        .salary(1000.0)
                        .dob(LocalDate.of(2000, 1, 1))
                        .build());

        Mockito.when(inventoryServiceClient.getInventoryById(VALID_INVENTORY_ID))
                .thenReturn(BookResponseModel.builder()
                        .bookid(VALID_INVENTORY_ID)
                        .authorid(VALID_AUTHOR_ID)
                        .title("The Book")
                        .genre("Fiction")
                        .publisher("Penguin")
                        .released(LocalDateTime.now())
                        .stock(10)
                        .build());

        Mockito.when(membershipServiceClient.getMemberById(VALID_MEMBER_ID))
                .thenReturn(MemberResponseModel.builder()
                        .memberid(VALID_MEMBER_ID)
                        .firstName("Isaac")
                        .lastName("Wallace")
                        .email("IsaacWallace@me.com")
                        .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                        .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                        .build());
    }

    /*--> Header Tests <--*/

    @Test
    void whenUnsupportedMediaTypeRequested_thenReturnNotAcceptable() {
        this.webTestClient.get()
                .uri(SERVICE_URI, VALID_MEMBER_ID)
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    /*--> Validation Tests <--*/

    @Test
    void whenBulkTransactionsAreCreated_thenAllAreSavedCorrectly() {
        this.transactionRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            TransactionRequestModel model = TransactionRequestModel.builder()
                    .memberid(VALID_MEMBER_ID)
                    .bookid(VALID_INVENTORY_ID)
                    .employeeid(VALID_EMPLOYEE_ID)
                    .status(Status.PENDING)
                    .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                    .transactionDate(LocalDateTime.now())
                    .build();

            this.webTestClient.post()
                    .uri(SERVICE_URI, VALID_MEMBER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(model)
                    .exchange()
                    .expectStatus().isCreated();
        }

        this.webTestClient.get()
                .uri(SERVICE_URI, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseModel.class)
                .value(transactions -> assertEquals(10, transactions.size()));
    }

    @Test
    void getAllTransactions_emptyDB_returnEmptyList() {
        this.transactionRepository.deleteAll();

        this.webTestClient.get()
                .uri(SERVICE_URI, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MemberResponseModel.class)
                .value(list -> assertTrue(list.isEmpty()));
    }

    /*--> RequestModel Tests <--*/

    @Test
    void testEqualsAndHashCodeOnRequestModel() {
        TransactionRequestModel model1 = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionRequestModel model2 = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionRequestModel modelDifferent = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        assertEquals(model1, model2);
        assertNotEquals(model1, modelDifferent);
        assertNotEquals(model2, modelDifferent);

        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), modelDifferent.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
    }

    @Test
    void testGettersOnRequestModel() {
        LocalDateTime now = LocalDateTime.now();

        TransactionRequestModel model = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(now)
                .build();

        assertEquals(VALID_MEMBER_ID, model.getMemberid());
        assertEquals(VALID_INVENTORY_ID, model.getBookid());
        assertEquals(VALID_EMPLOYEE_ID, model.getEmployeeid());
        assertEquals(Status.PENDING, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    @Test
    void testSettersOnRequestModel() {
        LocalDateTime now = LocalDateTime.now();

        TransactionRequestModel model = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        model.setMemberid(NOT_FOUND_ID);
        model.setBookid(NOT_FOUND_ID);
        model.setEmployeeid(NOT_FOUND_ID);
        model.setStatus(Status.COMPLETED);
        model.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));
        model.setTransactionDate(now);

        assertEquals(NOT_FOUND_ID, model.getMemberid());
        assertEquals(NOT_FOUND_ID, model.getBookid());
        assertEquals(NOT_FOUND_ID, model.getEmployeeid());
        assertEquals(Status.COMPLETED, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    @Test
    void testRequestModelConstructor() {
        LocalDateTime now = LocalDateTime.now();

        TransactionRequestModel model = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(now)
                .build();

        assertEquals(VALID_MEMBER_ID, model.getMemberid());
        assertEquals(VALID_INVENTORY_ID, model.getBookid());
        assertEquals(VALID_EMPLOYEE_ID, model.getEmployeeid());
        assertEquals(Status.PENDING, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    @Test
    void testRequestModelToString() {
        TransactionRequestModel model = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        assertTrue(model.toString().contains(VALID_EMPLOYEE_ID));
        assertTrue(model.toString().contains(VALID_MEMBER_ID));
        assertTrue(model.toString().contains(VALID_INVENTORY_ID));
        assertTrue(model.toString().contains(Status.PENDING.toString()));
        assertTrue(model.toString().contains(new Payment(Method.CASH, Currency.CAD, 9.99).toString()));
    }

    @Test
    void testRequestModelSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        TransactionRequestModel model = TransactionRequestModel.builder().build();

        model.setMemberid(VALID_MEMBER_ID);
        model.setBookid(VALID_INVENTORY_ID);
        model.setEmployeeid(VALID_EMPLOYEE_ID);
        model.setStatus(Status.PENDING);
        model.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));
        model.setTransactionDate(now);

        assertEquals(VALID_MEMBER_ID, model.getMemberid());
        assertEquals(VALID_INVENTORY_ID, model.getBookid());
        assertEquals(VALID_EMPLOYEE_ID, model.getEmployeeid());
        assertEquals(Status.PENDING, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    /*--> ResponseModel Tests <--*/

    @Test
    void testEqualsAndHashCodeOnResponseModel() {
        TransactionResponseModel model1 = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionResponseModel model2 = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionResponseModel modelDifferent = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        assertEquals(model1, model2);
        assertNotEquals(model1, modelDifferent);
        assertNotEquals(model2, modelDifferent);

        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), modelDifferent.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
    }

    @Test
    void testGettersOnResponseModel() {
        LocalDateTime now = LocalDateTime.now();

        TransactionResponseModel model = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(now)
                .build();

        assertEquals(VALID_TRANSACTION_ID, model.getTransactionid());
        assertEquals(VALID_MEMBER_ID, model.getMemberid());
        assertEquals(VALID_INVENTORY_ID, model.getBookid());
        assertEquals(VALID_EMPLOYEE_ID, model.getEmployeeid());
        assertEquals(Status.PENDING, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    @Test
    void testSettersOnResponseModel() {
        LocalDateTime now = LocalDateTime.now();

        TransactionResponseModel model = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        model.setTransactionid(NOT_FOUND_ID);
        model.setMemberid(NOT_FOUND_ID);
        model.setBookid(NOT_FOUND_ID);
        model.setEmployeeid(NOT_FOUND_ID);
        model.setStatus(Status.COMPLETED);
        model.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));
        model.setTransactionDate(now);

        assertEquals(NOT_FOUND_ID, model.getTransactionid());
        assertEquals(NOT_FOUND_ID, model.getMemberid());
        assertEquals(NOT_FOUND_ID, model.getBookid());
        assertEquals(NOT_FOUND_ID, model.getEmployeeid());
        assertEquals(Status.COMPLETED, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    @Test
    void testResponseModelConstructor() {
        TransactionResponseModel model = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        assertEquals(VALID_TRANSACTION_ID, model.getTransactionid());
        assertEquals(VALID_MEMBER_ID, model.getMemberid());
        assertEquals(VALID_INVENTORY_ID, model.getBookid());
        assertEquals(VALID_EMPLOYEE_ID, model.getEmployeeid());
        assertEquals(Status.PENDING, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
    }

    @Test
    void testResponseModelToString() {
        TransactionResponseModel model = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        assertTrue(model.toString().contains(VALID_TRANSACTION_ID));
        assertTrue(model.toString().contains(VALID_EMPLOYEE_ID));
        assertTrue(model.toString().contains(VALID_MEMBER_ID));
        assertTrue(model.toString().contains(VALID_INVENTORY_ID));
        assertTrue(model.toString().contains(Status.PENDING.toString()));
        assertTrue(model.toString().contains(new Payment(Method.CASH, Currency.CAD, 9.99).toString()));
    }

    @Test
    void testSetTransactionIdOnResponseModel() {
        TransactionResponseModel model = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        model.setTransactionid(NOT_FOUND_ID);

        assertEquals(NOT_FOUND_ID, model.getTransactionid());
    }

    @Test
    void testResponseModelSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        TransactionResponseModel model = TransactionResponseModel.builder().build();

        model.setTransactionid(NOT_FOUND_ID);
        model.setMemberid(NOT_FOUND_ID);
        model.setBookid(NOT_FOUND_ID);
        model.setEmployeeid(NOT_FOUND_ID);
        model.setStatus(Status.COMPLETED);
        model.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));
        model.setTransactionDate(now);

        assertEquals(NOT_FOUND_ID, model.getTransactionid());
        assertEquals(NOT_FOUND_ID, model.getMemberid());
        assertEquals(NOT_FOUND_ID, model.getBookid());
        assertEquals(NOT_FOUND_ID, model.getEmployeeid());
        assertEquals(Status.COMPLETED, model.getStatus());
        assertEquals(new Payment(Method.CASH, Currency.CAD, 9.99), model.getPayment());
        assertEquals(now, model.getTransactionDate());
    }

    /*--> CRUD Tests <--*/

    @Test
    void whenTransactionExists_thenReturnAllTransactions() {
        long sizeDB = this.transactionRepository.count();

        this.webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TransactionResponseModel.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertNotEquals(0, sizeDB);
                    assertEquals(sizeDB, list.size());

                    list.forEach((transactionResponseModel) -> {
                        assertNotNull(transactionResponseModel);
                        assertNotNull(transactionResponseModel.getTransactionid());
                        assertNotNull(transactionResponseModel.getMemberid());
                        assertNotNull(transactionResponseModel.getBookid());
                        assertNotNull(transactionResponseModel.getEmployeeid());
                        assertNotNull(transactionResponseModel.getStatus());
                        assertNotNull(transactionResponseModel.getPayment());
                        assertNotNull(transactionResponseModel.getTransactionDate());
                    });
                });
    }

    @Test
    void whenTransactionIdExists_thenReturnIsOk() {
        this.webTestClient.get()
            .uri(BASE_URI + "/" + VALID_TRANSACTION_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(TransactionResponseModel.class)
            .value((transactionResponseModel) -> {
                assertNotNull(transactionResponseModel);
                assertNotNull(transactionResponseModel.getTransactionid());
                assertNotNull(transactionResponseModel.getMemberid());
                assertNotNull(transactionResponseModel.getBookid());
                assertNotNull(transactionResponseModel.getEmployeeid());
                assertNotNull(transactionResponseModel.getStatus());
                assertNotNull(transactionResponseModel.getPayment());
                assertNotNull(transactionResponseModel.getTransactionDate());
            });
    }

    @Test
    void whenGetMemberTransactionById_thenReturnTransaction() {
        this.webTestClient.get()
                .uri(SERVICE_URI + "/" + VALID_TRANSACTION_ID, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TransactionResponseModel.class)
                .hasSize(1);
    }

    @Test
    void whenTransactionExistsOnCreate_thenReturnIsCreated() {
        TransactionRequestModel transactionRequestModel = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        this.webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TransactionResponseModel.class)
                .value((transactionResponseModel) -> {
                    assertNotNull(transactionResponseModel);
                    assertNotNull(transactionResponseModel.getTransactionid());
                    assertNotNull(transactionResponseModel.getMemberid());
                    assertNotNull(transactionResponseModel.getBookid());
                    assertNotNull(transactionResponseModel.getEmployeeid());
                    assertNotNull(transactionResponseModel.getStatus());
                    assertNotNull(transactionResponseModel.getPayment());
                    assertNotNull(transactionResponseModel.getTransactionDate());
                });
    }

    @Test
    void whenTransactionExistsOnUpdate_thenReturnIsOk() {
        TransactionRequestModel transactionRequestModel = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        this.webTestClient.put()
                .uri(BASE_URI + "/" + VALID_TRANSACTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TransactionResponseModel.class)
                .value((transactionResponseModel) -> {
                    assertNotNull(transactionResponseModel);
                    assertNotNull(transactionResponseModel.getTransactionid());
                    assertNotNull(transactionResponseModel.getMemberid());
                    assertNotNull(transactionResponseModel.getBookid());
                    assertNotNull(transactionResponseModel.getEmployeeid());
                    assertNotNull(transactionResponseModel.getStatus());
                    assertNotNull(transactionResponseModel.getPayment());
                    assertNotNull(transactionResponseModel.getTransactionDate());
                });
    }

    @Test
    void whenMemberTransactionExistsOnUpdate_thenReturnIsOk() {
        TransactionRequestModel transactionRequestModel = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.PENDING)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        this.webTestClient.put()
                .uri(SERVICE_URI + "/" + VALID_TRANSACTION_ID, VALID_MEMBER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TransactionResponseModel.class)
                .value((transactionResponseModel) -> {
                    assertNotNull(transactionResponseModel);
                    assertNotNull(transactionResponseModel.getTransactionid());
                    assertNotNull(transactionResponseModel.getMemberid());
                    assertNotNull(transactionResponseModel.getBookid());
                    assertNotNull(transactionResponseModel.getEmployeeid());
                    assertNotNull(transactionResponseModel.getStatus());
                    assertNotNull(transactionResponseModel.getPayment());
                    assertNotNull(transactionResponseModel.getTransactionDate());
                });
    }

    @Test
    void whenTransactionExistsOnDelete_thenReturnIsNoContent() {
        this.webTestClient.delete()
                .uri(BASE_URI + "/" + VALID_TRANSACTION_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenMemberTransactionExistsOnDelete_thenReturnIsNoContent() {
        this.webTestClient.delete()
                .uri(SERVICE_URI + "/" + VALID_TRANSACTION_ID, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    /*--> GET Tests <--*/

    @Test
    void whenTransactionIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
                .uri(BASE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown transactionid: " + NOT_FOUND_ID);
    }

    @Test
    void whenTransactionIdIsInvalidOnGet_thenReturnUnprocessableEntity() {
        this.webTestClient.get()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid transactionid: " + INVALID_ID);
    }

    /*--> DELETE Tests <---*/

    @Test
    void whenTransactionIdIsNotFoundOnDelete_thenReturnNotFound() {
        this.webTestClient.delete()
                .uri(BASE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown transactionid: " + NOT_FOUND_ID);
    }

    @Test
    void whenTransactionIdIsInvalidOnDelete_thenReturnUnprocessableEntity() {
        this.webTestClient.delete()
                .uri(BASE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid transactionid: " + INVALID_ID);
    }

    @Test
    void whenDeleteTransactionsByInventory_thenAllMatchingTransactionsAreDeleted() {
        this.transactionRepository.deleteAll();

        for (int i = 0; i < 3; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionIdentifier(new TransactionIdentifier());
            transaction.setBookid(VALID_INVENTORY_ID);
            transaction.setMemberid(VALID_MEMBER_ID);
            transaction.setEmployeeid(VALID_EMPLOYEE_ID);
            transaction.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setStatus(Status.COMPLETED);
            transactionRepository.save(transaction);
        }

        assertEquals(3, transactionRepository.count());

        this.webTestClient.delete()
                .uri(BASE_URI + "/inventory/{bookid}", VALID_INVENTORY_ID)
                .exchange()
                .expectStatus().isNoContent();

        assertEquals(0, transactionRepository.count());
    }

    @Test
    void whenDeleteTransactionsByEmployee_thenAllMatchingTransactionsAreDeleted() {
        this.transactionRepository.deleteAll();

        for (int i = 0; i < 2; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionIdentifier(new TransactionIdentifier());
            transaction.setBookid(VALID_INVENTORY_ID);
            transaction.setMemberid(VALID_MEMBER_ID);
            transaction.setEmployeeid(VALID_EMPLOYEE_ID);
            transaction.setPayment(new Payment(Method.CASH, Currency.CAD, 10.00));
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setStatus(Status.PENDING);
            transactionRepository.save(transaction);
        }

        assertEquals(2, transactionRepository.count());

        this.webTestClient.delete()
                .uri(BASE_URI + "/employee/{employeeid}", VALID_EMPLOYEE_ID)
                .exchange()
                .expectStatus().isNoContent();

        assertEquals(0, transactionRepository.count());
    }

    @Test
    void whenDeleteTransactionsByMember_thenAllMatchingTransactionsAreDeleted() {
        this.transactionRepository.deleteAll();

        for (int i = 0; i < 4; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionIdentifier(new TransactionIdentifier());
            transaction.setBookid(VALID_INVENTORY_ID);
            transaction.setMemberid(VALID_MEMBER_ID);
            transaction.setEmployeeid(VALID_EMPLOYEE_ID);
            transaction.setPayment(new Payment(Method.CASH, Currency.CAD, 12.34));
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setStatus(Status.COMPLETED);
            transactionRepository.save(transaction);
        }

        assertEquals(4, transactionRepository.count());

        this.webTestClient.delete()
                .uri(BASE_URI + "/member/{memberid}", VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isNoContent();

        assertEquals(0, transactionRepository.count());
    }

}
