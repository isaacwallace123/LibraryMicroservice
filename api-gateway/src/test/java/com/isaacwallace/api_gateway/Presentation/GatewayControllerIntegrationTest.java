package com.isaacwallace.api_gateway.Presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isaacwallace.api_gateway.DomainClient.*;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorResponseModel;
import com.isaacwallace.api_gateway.Services.Employee.DataAccess.Title;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryResponseModel;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.Address;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.Phone;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.PhoneType;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipResponseModel;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Currency;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Method;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Payment;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Status;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayControllerIntegrationTest {
    private final String BASE_URI_EMPLOYEES = "/api/v1/employees";
    private final String BASE_URI_MEMBER = "/api/v1/members";
    private final String BASE_URI_INVENTORY = "/api/v1/inventory";
    private final String BASE_URI_TRANSACTION = "/api/v1/transactions";
    private final String BASE_URI_AUTHORS = "/api/v1/authors";
    private final String BASE_URI_MEMBER_TRANSACTION = "/api/v1/members/{memberid}/transactions";

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";

    private final String VALID_TRANSACTION_ID = "8b0a2c1b-734f-4cb1-b123-5f1e3e2b5a4f";
    private final String VALID_MEMBER_ID = "823e4567-e89b-12d3-a456-556642440007";
    private final String VALID_INVENTORY_ID = "c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5";
    private final String VALID_EMPLOYEE_ID = "61d2d9f8-e144-4984-8bcb-7fa29ef4fdf6";
    private final String VALID_AUTHOR_ID = "123e4567-e89b-12d3-a456-556642440000";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private MockRestServiceServer mockRestServiceServer;

    @MockitoBean
    private MembershipServiceClient membershipServiceClient;

    @MockitoBean
    private EmployeeServiceClient employeeServiceClient;

    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;

    @MockitoBean
    private AuthorServiceClient authorServiceClient;

    @MockitoBean
    private TransactionServiceClient transactionServiceClient;

    @BeforeEach
    public void setup() {
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setupMocks() {
        AuthorResponseModel authorResponseModel = AuthorResponseModel.builder()
                .authorid(VALID_AUTHOR_ID)
                .firstName("Jane")
                .lastName("Austen")
                .pseudonym("N/A")
                .build();

        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
                .firstName("Jane")
                .lastName("Austen")
                .pseudonym(null)
                .build();

        /*-----------------------------------------------------------------------*/

        InventoryResponseModel inventoryResponseModel = InventoryResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid(VALID_AUTHOR_ID)
                .author(authorResponseModel)
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        InventoryRequestModel inventoryRequestModel = InventoryRequestModel.builder()
                .authorid(VALID_AUTHOR_ID)
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        /*-----------------------------------------------------------------------*/

        MembershipResponseModel membershipResponseModel = MembershipResponseModel.builder()
                .memberid(VALID_MEMBER_ID)
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        MembershipRequestModel membershipRequestModel = MembershipRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        /*-----------------------------------------------------------------------*/

        EmployeeResponseModel employeeResponseModel = EmployeeResponseModel.builder()
                .employeeid(VALID_EMPLOYEE_ID)
                .firstName("Isaac")
                .lastName("Wallace")
                .age(30)
                .title(Title.ADMINISTRATOR)
                .email("IsaacWallace@me.com")
                .salary(1000.0)
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        EmployeeRequestModel employeeRequestModel = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("IsaacWallace@me.com")
                .salary(1000.0)
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        /*-----------------------------------------------------------------------*/

        TransactionResponseModel transactionResponseModel = TransactionResponseModel.builder()
                .transactionid(VALID_TRANSACTION_ID)
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.COMPLETED)
                .transactionDate(LocalDateTime.now())
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .book(inventoryResponseModel)
                .employee(employeeResponseModel)
                .member(membershipResponseModel)
                .build();

        TransactionRequestModel transactionRequestModel = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .bookid(VALID_INVENTORY_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        /*-----------------------------------------------------------------------*/

        Mockito.when(membershipServiceClient.getMemberByMemberId(VALID_MEMBER_ID)).thenReturn(membershipResponseModel);
        Mockito.when(membershipServiceClient.getMembers()).thenReturn(List.of(membershipResponseModel));
        Mockito.when(membershipServiceClient.addMember(membershipRequestModel)).thenReturn(membershipResponseModel);
        Mockito.when(membershipServiceClient.updateMember(VALID_MEMBER_ID, membershipRequestModel)).thenReturn(membershipResponseModel);
        Mockito.doNothing().when(membershipServiceClient).deleteMember(VALID_MEMBER_ID);

        /*-----------------------------------------------------------------------*/

        Mockito.when(employeeServiceClient.getEmployeeByEmployeeId(VALID_EMPLOYEE_ID)).thenReturn(employeeResponseModel);
        Mockito.when(employeeServiceClient.getEmployees()).thenReturn(List.of(employeeResponseModel));
        Mockito.when(employeeServiceClient.addEmployee(employeeRequestModel)).thenReturn(employeeResponseModel);
        Mockito.when(employeeServiceClient.updateEmployee(VALID_EMPLOYEE_ID, employeeRequestModel)).thenReturn(employeeResponseModel);
        Mockito.doNothing().when(employeeServiceClient).deleteEmployee(VALID_EMPLOYEE_ID);

        /*-----------------------------------------------------------------------*/

        Mockito.when(inventoryServiceClient.getInventoryByInventoryId(VALID_INVENTORY_ID)).thenReturn(inventoryResponseModel);
        Mockito.when(inventoryServiceClient.getInventorys()).thenReturn(List.of(inventoryResponseModel));
        Mockito.when(inventoryServiceClient.addInventory(Mockito.any(InventoryRequestModel.class))).thenReturn(inventoryResponseModel);
        Mockito.when(inventoryServiceClient.updateInventory(eq(VALID_INVENTORY_ID), Mockito.any(InventoryRequestModel.class))).thenReturn(inventoryResponseModel);

        Mockito.doNothing().when(inventoryServiceClient).deleteInventory(VALID_INVENTORY_ID);

        /*-----------------------------------------------------------------------*/

        Mockito.when(authorServiceClient.getAuthorByAuthorId(VALID_AUTHOR_ID)).thenReturn(authorResponseModel);
        Mockito.when(authorServiceClient.getAuthors()).thenReturn(List.of(authorResponseModel));
        Mockito.when(authorServiceClient.addAuthor(authorRequestModel)).thenReturn(authorResponseModel);
        Mockito.when(authorServiceClient.updateAuthor(VALID_AUTHOR_ID, authorRequestModel)).thenReturn(authorResponseModel);
        Mockito.doNothing().when(authorServiceClient).deleteAuthor(VALID_AUTHOR_ID);

        /*-----------------------------------------------------------------------*/

        Mockito.when(transactionServiceClient.getMemberTransactionById(VALID_MEMBER_ID, VALID_TRANSACTION_ID)).thenReturn(transactionResponseModel);
        Mockito.when(transactionServiceClient.getTransactionsFromMember(VALID_MEMBER_ID)).thenReturn(List.of(transactionResponseModel));

        Mockito.when(transactionServiceClient.getTransactionByTransactionId(VALID_TRANSACTION_ID)).thenReturn(transactionResponseModel);
        Mockito.when(transactionServiceClient.getTransactions()).thenReturn(List.of(transactionResponseModel));
        Mockito.when(transactionServiceClient.addTransaction(Mockito.any(TransactionRequestModel.class))).thenReturn(transactionResponseModel);
        Mockito.when(transactionServiceClient.updateTransaction(eq(VALID_TRANSACTION_ID), Mockito.any(TransactionRequestModel.class))).thenReturn(transactionResponseModel);
        Mockito.doNothing().when(transactionServiceClient).deleteTransaction(VALID_TRANSACTION_ID);
    }

    /*--> GET Tests <--*/

    @Test
    void testGetMemberTransactionsByMemberId() {
        webTestClient.get()
                .uri(BASE_URI_MEMBER_TRANSACTION, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseModel.class)
                .value(list -> {
                    assertEquals(1, list.size());
                    assertEquals(VALID_TRANSACTION_ID, list.get(0).getTransactionid());
                });
    }

    @Test
    void testGetMemberTransactionByTransactionId() {
        webTestClient.get()
                .uri(BASE_URI_MEMBER_TRANSACTION + "/" + VALID_TRANSACTION_ID, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.member.firstName").isEqualTo("Isaac")
                .jsonPath("$.employee.title").isEqualTo("ADMINISTRATOR")
                .jsonPath("$.book.genre").isEqualTo("Fiction")
                .jsonPath("$.book.author.lastName").isEqualTo("Austen")
                .jsonPath("$.status").isEqualTo("COMPLETED");
    }

    @Test
    void testGetAllTransactions() {
        webTestClient.get()
                .uri(BASE_URI_TRANSACTION)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseModel.class)
                .value(list -> {
                    assertEquals(1, list.size());
                    assertEquals(VALID_TRANSACTION_ID, list.get(0).getTransactionid());
                });
    }

    @Test
    void testGetTransactionByTransactionId() {
        webTestClient.get()
                .uri(BASE_URI_TRANSACTION + "/" + VALID_TRANSACTION_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.member.firstName").isEqualTo("Isaac")
                .jsonPath("$.employee.title").isEqualTo("ADMINISTRATOR")
                .jsonPath("$.book.genre").isEqualTo("Fiction")
                .jsonPath("$.book.author.lastName").isEqualTo("Austen")
                .jsonPath("$.status").isEqualTo("COMPLETED");
    }

    @Test
    void testGetAllMembers() {
        webTestClient.get()
                .uri(BASE_URI_MEMBER)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MembershipResponseModel.class)
                .value(list -> {
                    assertEquals(1, list.size());
                    assertEquals(VALID_MEMBER_ID, list.get(0).getMemberid());
                });
    }

    @Test
    void testGetMemberByMemberId() {
        webTestClient.get()
                .uri(BASE_URI_MEMBER + "/" + VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Isaac")
                .jsonPath("$.lastName").isEqualTo("Wallace");
    }

    @Test
    void testGetAllEmployees() {
        webTestClient.get()
                .uri(BASE_URI_EMPLOYEES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeResponseModel.class)
                .value(list -> {
                    assertEquals(1, list.size());
                    assertEquals(VALID_EMPLOYEE_ID, list.get(0).getEmployeeid());
                });
    }

    @Test
    void testGetEmployeeByEmployeeId() {
        webTestClient.get()
                .uri(BASE_URI_EMPLOYEES + "/" + VALID_EMPLOYEE_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Isaac")
                .jsonPath("$.lastName").isEqualTo("Wallace");
    }

    @Test
    void testGetAllInventorys() {
        webTestClient.get()
                .uri(BASE_URI_INVENTORY)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryResponseModel.class)
                .value(list -> {
                    assertEquals(1, list.size());
                    assertEquals(VALID_INVENTORY_ID, list.get(0).getBookid());
                });
    }

    @Test
    void testGetInventoryByInventoryId() {
        webTestClient.get()
                .uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("The Book")
                .jsonPath("$.author.lastName").isEqualTo("Austen");
    }

    @Test
    void testGetAllAuthors() {
        webTestClient.get()
                .uri(BASE_URI_AUTHORS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorResponseModel.class)
                .value(list -> {
                    assertEquals(1, list.size());
                    assertEquals(VALID_AUTHOR_ID, list.get(0).getAuthorid());
                });
    }

    @Test
    void testGetAuthorByAuthorId() {
        webTestClient.get()
                .uri(BASE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Jane")
                .jsonPath("$.lastName").isEqualTo("Austen");
    }

    /*--> POST Tests <--*/

    @Test
    void testCreateMember() {
        MembershipRequestModel request = MembershipRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        webTestClient.post()
                .uri(BASE_URI_MEMBER)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Isaac");
    }

    @Test
    void testCreateEmployee() {
        EmployeeRequestModel request = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("IsaacWallace@me.com")
                .salary(1000.0)
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        webTestClient.post()
                .uri(BASE_URI_EMPLOYEES)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("ADMINISTRATOR")
                .jsonPath("$.email").isEqualTo("IsaacWallace@me.com");
    }

    @Test
    void testCreateInventory() {
        InventoryRequestModel request = InventoryRequestModel.builder()
                .authorid(VALID_AUTHOR_ID)
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        webTestClient.post()
                .uri(BASE_URI_INVENTORY)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("The Book")
                .jsonPath("$.genre").isEqualTo("Fiction");
    }

    @Test
    void testCreateAuthor() {
        AuthorRequestModel request = AuthorRequestModel.builder()
                .firstName("Jane")
                .lastName("Austen")
                .pseudonym(null)
                .build();

        webTestClient.post()
                .uri("/api/v1/authors")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.lastName").isEqualTo("Austen");
    }

    @Test
    void testCreateTransaction() {
        TransactionRequestModel request = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .bookid(VALID_INVENTORY_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        webTestClient.post()
                .uri(BASE_URI_TRANSACTION)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("COMPLETED")
                .jsonPath("$.payment.currency").isEqualTo("CAD");
    }

    @Test
    void testCreateTransactionFromMember() {
        TransactionRequestModel request = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .bookid(VALID_INVENTORY_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        webTestClient.post()
                .uri(BASE_URI_MEMBER_TRANSACTION, VALID_MEMBER_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("COMPLETED")
                .jsonPath("$.payment.currency").isEqualTo("CAD");
    }

    /*--> PUT Tests <--*/

    @Test
    void testUpdateAuthor() {
        AuthorRequestModel request = AuthorRequestModel.builder()
                .firstName("Jane")
                .lastName("Austen")
                .pseudonym(null)
                .build();

        webTestClient.put()
                .uri(BASE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lastName").isEqualTo("Austen");
    }

    @Test
    void testUpdateMember() {
        MembershipRequestModel request = MembershipRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        webTestClient.put()
                .uri(BASE_URI_MEMBER + "/" + VALID_MEMBER_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Isaac");
    }

    @Test
    void testUpdateEmployee() {
        EmployeeRequestModel request = EmployeeRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .title(Title.ADMINISTRATOR)
                .email("IsaacWallace@me.com")
                .salary(1000.0)
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        webTestClient.put()
                .uri(BASE_URI_EMPLOYEES + "/" + VALID_EMPLOYEE_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("ADMINISTRATOR")
                .jsonPath("$.email").isEqualTo("IsaacWallace@me.com");
    }

    @Test
    void testUpdateInventory() {
        InventoryRequestModel request = InventoryRequestModel.builder()
                .authorid(VALID_AUTHOR_ID)
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        webTestClient.put()
                .uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("The Book")
                .jsonPath("$.genre").isEqualTo("Fiction");
    }

    @Test
    void testUpdateTransaction() {
        TransactionRequestModel request = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .bookid(VALID_INVENTORY_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        webTestClient.put()
                .uri(BASE_URI_TRANSACTION + "/" + VALID_TRANSACTION_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("COMPLETED")
                .jsonPath("$.payment.currency").isEqualTo("CAD");
    }

    @Test
    void testUpdateTransactionFromMember() {
        TransactionRequestModel request = TransactionRequestModel.builder()
                .memberid(VALID_MEMBER_ID)
                .employeeid(VALID_EMPLOYEE_ID)
                .bookid(VALID_INVENTORY_ID)
                .status(Status.COMPLETED)
                .payment(new Payment(Method.CASH, Currency.CAD, 9.99))
                .transactionDate(LocalDateTime.now())
                .build();

        webTestClient.put()
                .uri(BASE_URI_MEMBER_TRANSACTION + "/" + VALID_TRANSACTION_ID, VALID_MEMBER_ID)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("COMPLETED")
                .jsonPath("$.payment.currency").isEqualTo("CAD");
    }

    /*--> DELETE Tests <--*/

    @Test
    void testDeleteAuthor() {
        webTestClient.delete()
                .uri(BASE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteMember() {
        webTestClient.delete()
                .uri(BASE_URI_MEMBER + "/" + VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteEmployee() {
        webTestClient.delete()
                .uri(BASE_URI_EMPLOYEES + "/" + VALID_EMPLOYEE_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteInventory() {
        webTestClient.delete()
                .uri(BASE_URI_INVENTORY + "/" + VALID_INVENTORY_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteTransaction() {
        webTestClient.delete()
                .uri(BASE_URI_TRANSACTION + "/" + VALID_TRANSACTION_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteTransactionFromMember() {
        webTestClient.delete()
                .uri(BASE_URI_MEMBER_TRANSACTION + "/" + VALID_TRANSACTION_ID, VALID_MEMBER_ID)
                .exchange()
                .expectStatus().isNoContent();
    }
}
