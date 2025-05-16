package com.isaacwallace.transaction_service.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isaacwallace.transaction_service.DomainClient.Inventory.InventoryServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Inventory.Models.BookResponseModel;
import com.isaacwallace.transaction_service.Utils.Exceptions.DuplicateResourceException;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("test")
public class InventoryServiceClientTest {
    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_INVENTORY_ID = "c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5";

    private final String BASE_URL = "http://localhost:8080/api/v1/inventory";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private InventoryServiceClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        this.server = MockRestServiceServer.createServer(restTemplate);
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.client = new InventoryServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void testInventoryResponseModelEquality() {
        BookResponseModel mockBook = BookResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid("auth-123")
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        BookResponseModel mockBook2 = BookResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid("auth-123")
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        assertEquals(mockBook, mockBook2);
    }

    @Test
    void testInventoryResponseModelHashCode() {
        BookResponseModel mockBook = BookResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid("auth-123")
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        BookResponseModel mockBook2 = BookResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid("auth-123")
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        assertEquals(mockBook.hashCode(), mockBook2.hashCode());
    }

    @Test
    void testGetInventoryById_fromServiceClient() throws Exception {
        BookResponseModel mockBook = BookResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid("auth-123")
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        String json = objectMapper.writeValueAsString(mockBook);

        server.expect(requestTo(BASE_URL + "/" + VALID_INVENTORY_ID))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        BookResponseModel response = client.getInventoryById(VALID_INVENTORY_ID);

        assertNotNull(response);
        assertEquals(VALID_INVENTORY_ID, response.getBookid());
        assertEquals("The Book", response.getTitle());

        server.verify();
    }

    @Test
    void testInventoryResponseModelToString() {
        BookResponseModel mockBook = BookResponseModel.builder()
                .bookid(VALID_INVENTORY_ID)
                .authorid("auth-123")
                .title("The Book")
                .genre("Fiction")
                .publisher("Penguin")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        String stringOutput = mockBook.toString();

        assertTrue(stringOutput.contains(VALID_INVENTORY_ID));
        assertTrue(stringOutput.contains("auth-123"));
        assertTrue(stringOutput.contains("The Book"));
        assertTrue(stringOutput.contains("Fiction"));
        assertTrue(stringOutput.contains("Penguin"));
        assertTrue(stringOutput.contains("5"));
    }

    @Test
    void whenInventoryNotFound_thenThrowNotFoundException() {
        server.expect(requestTo(BASE_URL + "/" + NOT_FOUND_ID))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getInventoryById(NOT_FOUND_ID));
    }

    @Test
    void whenInventoryIdIsInvalid_thenThrowInvalidInputException() {
        server.expect(requestTo(BASE_URL + "/" + INVALID_ID))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.getInventoryById(INVALID_ID));
    }

    @Test
    void whenConflictOccurs_thenThrowDuplicateResourceException() {
        server.expect(requestTo(BASE_URL + "/" + VALID_INVENTORY_ID))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.getInventoryById(VALID_INVENTORY_ID));
    }
}
