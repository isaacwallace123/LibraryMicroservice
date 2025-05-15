package com.isaacwallace.inventory_service.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.inventory_service.DomainClient.Transaction.TransactionServiceClient;
import com.isaacwallace.inventory_service.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.inventory_service.Utils.Exceptions.HttpErrorInfo;
import com.isaacwallace.inventory_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.inventory_service.Utils.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceClientTest {
    private final String VALID_ID = "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5";
    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";

    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;

    private TransactionServiceClient client;

    @BeforeEach
    void setup() {
        client = new TransactionServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void whenDeleteTransactionByInventoryId_thenRestTemplateDeleteCalled() {
        client.deleteTransactionByInventoryId(VALID_ID);
        verify(restTemplate).delete(contains(VALID_ID));
    }

    @Test
    void whenDeleteThrowsNotFound_thenNotFoundExceptionThrown() throws Exception {
        String json = "{\"message\":\"Not Found\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/transactions/inventory/" + NOT_FOUND_ID, "Not Found"));

        NotFoundException result = assertThrows(NotFoundException.class,
                () -> client.deleteTransactionByInventoryId(NOT_FOUND_ID));

        assertEquals("Not Found", result.getMessage());
    }

    @Test
    void whenDeleteThrowsInvalidInput_thenInvalidInputExceptionThrown() throws Exception {
        String json = "{\"message\":\"Invalid Input\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/transactions/inventory/" + INVALID_ID, "Invalid Input"));

        InvalidInputException thrown = assertThrows(InvalidInputException.class,
                () -> client.deleteTransactionByInventoryId(INVALID_ID));

        assertEquals("Invalid Input", thrown.getMessage());
    }

    @Test
    void whenDeleteThrowsConflict_thenDuplicateResourceExceptionThrown() throws Exception {
        String json = "{\"message\":\"Conflict\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.CONFLICT, "Conflict", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).delete(anyString());
        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.CONFLICT, "/api/v1/transactions/inventory/" + VALID_ID, "Conflict"));

        DuplicateResourceException thrown = assertThrows(DuplicateResourceException.class,
                () -> client.deleteTransactionByInventoryId(VALID_ID));

        assertEquals("Conflict", thrown.getMessage());
    }

    @Test
    void whenDeleteThrowsUnexpected_thenHttpClientErrorExceptionRethrown() {
        String json = "{\"message\":\"Server Error\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).delete(anyString());

        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class,
                () -> client.deleteTransactionByInventoryId("unexpected-id"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }
}
