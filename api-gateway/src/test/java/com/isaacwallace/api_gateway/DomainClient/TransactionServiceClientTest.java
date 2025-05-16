package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionResponseModel;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceClientTest {
    private final String BASE_URI_TRANSACTION = "http://localhost:8080/api/v1/transactions";
    private final String BASE_URI_MEMBER_TRANSACTION = "http://localhost:8080/api/v1/members";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private TransactionServiceClient client;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.client = new TransactionServiceClient(restTemplate, new ObjectMapper(), "localhost", "8080");
    }

    @Test
    void testGetTransactionById() throws Exception {
        String id = UUID.randomUUID().toString();
        TransactionResponseModel mockResponse = TransactionResponseModel.builder()
                .transactionid(id)
                .build();

        server.expect(requestTo(BASE_URI_TRANSACTION +"/" + id))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        TransactionResponseModel response = client.getTransactionByTransactionId(id);

        assertEquals(id, response.getTransactionid());
    }

    @Test
    void testGetAllTransactions() throws Exception {
        TransactionResponseModel transaction = TransactionResponseModel.builder()
                .transactionid(UUID.randomUUID().toString())
                .build();

        server.expect(requestTo(BASE_URI_TRANSACTION))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(new TransactionResponseModel[]{transaction}),
                        MediaType.APPLICATION_JSON
                ));

        assertEquals(1, client.getTransactions().size());
    }

    @Test
    void testGetTransactionsFromMember() throws Exception {
        String memberId = UUID.randomUUID().toString();

        TransactionResponseModel transaction = TransactionResponseModel.builder()
                .transactionid(UUID.randomUUID().toString())
                .memberid(memberId)
                .build();

        server.expect(requestTo(BASE_URI_MEMBER_TRANSACTION + "/" + memberId + "/transactions"))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(new TransactionResponseModel[]{transaction}),
                        MediaType.APPLICATION_JSON
                ));

        assertEquals(1, client.getTransactionsFromMember(memberId).size());
    }

    @Test
    void testGetMemberTransactionById() throws Exception {
        String memberId = UUID.randomUUID().toString();
        String transactionId = UUID.randomUUID().toString();

        TransactionResponseModel transaction = TransactionResponseModel.builder()
                .transactionid(transactionId)
                .memberid(memberId)
                .build();

        server.expect(requestTo(BASE_URI_MEMBER_TRANSACTION + "/" + memberId + "/transactions/" + transactionId))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(transaction),
                        MediaType.APPLICATION_JSON
                ));

        TransactionResponseModel response = client.getMemberTransactionById(memberId, transactionId);
        assertEquals(transactionId, response.getTransactionid());
    }

    @Test
    void testAddTransaction() {
        TransactionRequestModel request = TransactionRequestModel.builder().build();

        server.expect(requestTo(BASE_URI_TRANSACTION))
                .andRespond(withSuccess());

        client.addTransaction(request);
    }

    @Test
    void testUpdateTransaction() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        TransactionRequestModel request = TransactionRequestModel.builder().build();

        TransactionResponseModel updatedResponse = TransactionResponseModel.builder()
                .transactionid(transactionId)
                .build();

        server.expect(requestTo(BASE_URI_TRANSACTION + "/" + transactionId))
                .andRespond(withSuccess());

        server.expect(requestTo(BASE_URI_TRANSACTION + "/" + transactionId))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(updatedResponse),
                        MediaType.APPLICATION_JSON
                ));

        TransactionResponseModel response = client.updateTransaction(transactionId, request);
        assertEquals(transactionId, response.getTransactionid());
    }

    @Test
    void testDeleteTransaction() {
        String transactionId = UUID.randomUUID().toString();

        server.expect(requestTo( BASE_URI_TRANSACTION + "/" + transactionId))
                .andRespond(withSuccess());

        client.deleteTransaction(transactionId);
    }

    @Test
    void testGetTransactionById_NotFound() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI_TRANSACTION + "/" + id))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getTransactionByTransactionId(id));
    }

    @Test
    void testAddTransaction_InvalidInput() {
        TransactionRequestModel invalidRequest = TransactionRequestModel.builder().build();

        server.expect(requestTo(BASE_URI_TRANSACTION))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.addTransaction(invalidRequest));
    }

    @Test
    void testAddTransaction_Duplicate() {
        TransactionRequestModel duplicate = TransactionRequestModel.builder().build();

        server.expect(requestTo(BASE_URI_TRANSACTION))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.addTransaction(duplicate));
    }
}
