package com.isaacwallace.transaction_service.Presentation;

import com.isaacwallace.transaction_service.Business.TransactionService;
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
import com.isaacwallace.transaction_service.Mapper.TransactionRequestMapper;
import com.isaacwallace.transaction_service.Mapper.TransactionResponseMapper;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionControllerUnitTest {
    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";

    private final String VALID_TRANSACTION_ID = "8b0a2c1b-734f-4cb1-b123-5f1e3e2b5a4f";

    private final String VALID_MEMBER_ID = "823e4567-e89b-12d3-a456-556642440007";
    private final String VALID_INVENTORY_ID = "c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5";
    private final String VALID_EMPLOYEE_ID = "61d2d9f8-e144-4984-8bcb-7fa29ef4fdf6";
    private final String VALID_AUTHOR_ID = "123e4567-e89b-12d3-a456-556642440000";

    @Autowired
    private TransactionService transactionService;

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private EmployeeServiceClient employeeServiceClient;

    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;

    @MockitoBean
    private MembershipServiceClient membershipServiceClient;

    @MockitoSpyBean
    private TransactionResponseMapper transactionResponseMapper;

    @MockitoSpyBean
    private TransactionRequestMapper transactionRequestMapper;

    @Test
    void whenValidRequest_thenCreateTransactionSuccessfully() {
        TransactionRequestModel transactionRequestModel = TransactionRequestModel.builder()
            .memberid(VALID_MEMBER_ID)
            .bookid(VALID_INVENTORY_ID)
            .employeeid(VALID_EMPLOYEE_ID)
            .transactionDate(LocalDateTime.now())
            .payment(new Payment(Method.CASH, Currency.CAD, 10.00))
            .status(Status.COMPLETED)
        .build();

        EmployeeResponseModel employeeResponseModel = EmployeeResponseModel.builder()
            .employeeid(VALID_EMPLOYEE_ID)
            .firstName("Isaac")
            .lastName("Wallace")
            .age(30)
            .title(Title.ADMINISTRATOR)
            .email("IsaacWallace@me.com")
            .salary(1000.00)
            .dob(LocalDate.of(2000, 1, 1))
        .build();

        BookResponseModel bookResponseModel = BookResponseModel.builder()
            .bookid(VALID_INVENTORY_ID)
            .authorid(VALID_AUTHOR_ID)
            .genre("Fiction")
            .title("The Book")
            .publisher("Penguin")
            .released(LocalDateTime.now())
            .stock(10)
        .build();

        MemberResponseModel memberResponseModel = MemberResponseModel.builder()
            .memberid(VALID_MEMBER_ID)
            .firstName("Isaac")
            .lastName("Wallace")
            .email("IsaacWallace@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
        .build();

        Transaction transaction = new Transaction();
        transaction.setTransactionIdentifier(new TransactionIdentifier(VALID_TRANSACTION_ID));
        transaction.setBookid(bookResponseModel.getBookid());
        transaction.setMemberid(memberResponseModel.getMemberid());
        transaction.setEmployeeid(employeeResponseModel.getEmployeeid());
        transaction.setTransactionDate(transactionRequestModel.getTransactionDate());
        transaction.setPayment(transactionRequestModel.getPayment());
        transaction.setStatus(transactionRequestModel.getStatus());

        Mockito.when(employeeServiceClient.getEmployeeById(transactionRequestModel.getEmployeeid())).thenReturn(employeeResponseModel);
        Mockito.when(inventoryServiceClient.getInventoryById(transactionRequestModel.getBookid())).thenReturn(bookResponseModel);
        Mockito.when(membershipServiceClient.getMemberById(transactionRequestModel.getMemberid())).thenReturn(memberResponseModel);

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction savedTransaction = invocation.getArgument(0);

                    savedTransaction.setTransactionIdentifier(new TransactionIdentifier(VALID_TRANSACTION_ID));

                    return savedTransaction;
                });

        TransactionResponseModel response = transactionService.addTransaction(transactionRequestModel);

        assertThat(response).isNotNull();
        assertThat(response.getTransactionid()).isEqualTo(VALID_TRANSACTION_ID);
        assertThat(response.getBookid()).isEqualTo(VALID_INVENTORY_ID);
        assertThat(response.getMemberid()).isEqualTo(VALID_MEMBER_ID);
        assertThat(response.getEmployeeid()).isEqualTo(VALID_EMPLOYEE_ID);
        assertThat(response.getPayment()).isNotNull();
        assertThat(response.getPayment().getAmount()).isEqualTo(10.00);
        assertThat(response.getPayment().getCurrency()).isEqualTo(Currency.CAD);
        assertThat(response.getPayment().getMethod()).isEqualTo(Method.CASH);
        assertThat(response.getStatus()).isEqualTo(Status.COMPLETED);

        assertThat(response.getEmployee()).isNotNull();
        assertThat(response.getEmployee().getFirstName()).isEqualTo("Isaac");
        assertThat(response.getEmployee().getEmail()).isEqualTo("IsaacWallace@me.com");

        assertThat(response.getMember()).isNotNull();
        assertThat(response.getMember().getAddress().getCity()).isEqualTo("Anytown");

        assertThat(response.getBook()).isNotNull();
        assertThat(response.getBook().getTitle()).isEqualTo("The Book");

        Mockito.verify(employeeServiceClient, Mockito.atLeastOnce()).getEmployeeById(VALID_EMPLOYEE_ID);
        Mockito.verify(inventoryServiceClient, Mockito.atLeastOnce()).getInventoryById(VALID_INVENTORY_ID);
        Mockito.verify(membershipServiceClient, Mockito.atLeastOnce()).getMemberById(VALID_MEMBER_ID);
        Mockito.verify(transactionRepository).save(Mockito.any(Transaction.class));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        Mockito.verify(transactionRepository).save(captor.capture());
        Transaction savedTransaction = captor.getValue();

        assertThat(savedTransaction.getBookid()).isEqualTo(VALID_INVENTORY_ID);
        assertThat(savedTransaction.getMemberid()).isEqualTo(VALID_MEMBER_ID);
        assertThat(savedTransaction.getEmployeeid()).isEqualTo(VALID_EMPLOYEE_ID);
        assertThat(savedTransaction.getPayment().getAmount()).isEqualTo(10.00);
        assertThat(savedTransaction.getStatus()).isEqualTo(Status.COMPLETED);
    }
}
