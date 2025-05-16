package com.isaacwallace.api_gateway.Presentation;

import com.isaacwallace.api_gateway.DomainClient.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
public class GatewayControllerUnitTest {
    @MockitoBean
    private AuthorServiceClient authorServiceClient;
    @MockitoBean
    private EmployeeServiceClient employeeServiceClient;
    @MockitoBean
    private MembershipServiceClient membershipServiceClient;
    @MockitoBean
    private TransactionServiceClient transactionServiceClient;
    @MockitoBean
    private InventoryServiceClient inventoryServiceClient;


}
