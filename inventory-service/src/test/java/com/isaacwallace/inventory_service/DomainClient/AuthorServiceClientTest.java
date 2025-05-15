package com.isaacwallace.inventory_service.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.inventory_service.DomainClient.Author.AuthorServiceClient;
import com.isaacwallace.inventory_service.DomainClient.Author.Models.AuthorResponseModel;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceClientTest {

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_ID = "123e4567-e89b-12d3-a456-556642440000";

    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;

    private AuthorServiceClient authorServiceClient;

    @BeforeEach
    void setup() {
        authorServiceClient = new AuthorServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void whenGetAuthorById_thenRestTemplateCalled() {
        AuthorResponseModel dummyAuthor = AuthorResponseModel.builder()
                .authorId(VALID_ID)
                .firstName("John")
                .lastName("Doe")
                .pseudonym("J.D.")
                .build();

        when(restTemplate.getForObject(contains(VALID_ID), eq(AuthorResponseModel.class))).thenReturn(dummyAuthor);

        AuthorResponseModel result = authorServiceClient.getAuthorById(VALID_ID);

        assertEquals(dummyAuthor, result);
        verify(restTemplate).getForObject(contains(VALID_ID), eq(AuthorResponseModel.class));
    }

    @Test
    void whenGetAuthorThrowsNotFound_thenNotFoundExceptionThrown() throws Exception {
        String json = "{\"message\":\"Not Found\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).getForObject(anyString(), any());

        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/authors/" + NOT_FOUND_ID, "Not Found"));

        NotFoundException result = assertThrows(NotFoundException.class,
                () -> authorServiceClient.getAuthorById(NOT_FOUND_ID));

        assertEquals("Not Found", result.getMessage());
    }

    @Test
    void whenGetAuthorThrowsInvalidInput_thenInvalidInputExceptionThrown() throws Exception {
        String json = "{\"message\":\"Invalid Input\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).getForObject(anyString(), any());

        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "/api/v1/authors/" + INVALID_ID, "Invalid Input"));

        var thrown = assertThrows(InvalidInputException.class,
                () -> authorServiceClient.getAuthorById(INVALID_ID));

        assertEquals("Invalid Input", thrown.getMessage());
    }

    @Test
    void whenGetAuthorThrowsConflict_thenDuplicateResourceExceptionThrown() throws Exception {
        String json = "{\"message\":\"Conflict\"}";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.CONFLICT, "Conflict", null, json.getBytes(), null);

        doThrow(ex).when(restTemplate).getForObject(anyString(), any());

        when(objectMapper.readValue(json, HttpErrorInfo.class))
                .thenReturn(new HttpErrorInfo(HttpStatus.CONFLICT, "/api/v1/authors/" + VALID_ID, "Conflict"));

        var thrown = assertThrows(DuplicateResourceException.class,
                () -> authorServiceClient.getAuthorById(VALID_ID));

        assertEquals("Conflict", thrown.getMessage());
    }

    @Test
    void whenGetAuthorThrowsUnexpected_thenHttpClientErrorExceptionRethrown() {
        String authorId = "unexpected-id";
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null, null, null);

        doThrow(ex).when(restTemplate).getForObject(anyString(), any());

        var thrown = assertThrows(HttpClientErrorException.class,
                () -> authorServiceClient.getAuthorById(authorId));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    }

    @Test
    void testBuilderCreatesCorrectObject() {
        AuthorResponseModel model = AuthorResponseModel.builder()
                .authorId("auth-id")
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("I.W.")
                .build();

        assertEquals("auth-id", model.getAuthorId());
        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("I.W.", model.getPseudonym());
    }

    @Test
    void testEqualsAndHashCodeWithBuilder() {
        AuthorResponseModel a = AuthorResponseModel.builder()
                .authorId("1")
                .firstName("A")
                .lastName("B")
                .pseudonym("X")
                .build();

        AuthorResponseModel b = AuthorResponseModel.builder()
                .authorId("1")
                .firstName("A")
                .lastName("B")
                .pseudonym("X")
                .build();

        AuthorResponseModel c = AuthorResponseModel.builder()
                .authorId("2")
                .firstName("C")
                .lastName("D")
                .pseudonym("Y")
                .build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void testToStringWithBuilder() {
        AuthorResponseModel model = AuthorResponseModel.builder()
                .authorId("1")
                .firstName("A")
                .lastName("B")
                .pseudonym("X")
                .build();

        String result = model.toString();

        assertNotNull(result);
        assertTrue(result.contains("authorId=1"));
        assertTrue(result.contains("firstName=A"));
        assertTrue(result.contains("lastName=B"));
        assertTrue(result.contains("pseudonym=X"));
    }
}
