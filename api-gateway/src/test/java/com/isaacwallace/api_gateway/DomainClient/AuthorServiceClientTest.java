package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorResponseModel;
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
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
@ActiveProfiles("test")
public class AuthorServiceClientTest {
    private final String BASE_URI = "http://localhost:8080/api/v1/authors";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private AuthorServiceClient client;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.client = new AuthorServiceClient(restTemplate, new ObjectMapper(), "localhost", "8080");
    }

    @Test
    void testGetAuthorById() throws Exception {
        String id = UUID.randomUUID().toString();
        AuthorResponseModel mockResponse = AuthorResponseModel.builder()
                .authorid(id)
                .firstName("Jane")
                .lastName("Austen")
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        AuthorResponseModel response = client.getAuthorByAuthorId(id);
        assertEquals(id, response.getAuthorid());
    }

    @Test
    void testGetAllAuthors() throws Exception {
        AuthorResponseModel author = AuthorResponseModel.builder()
                .authorid(UUID.randomUUID().toString())
                .firstName("Mark")
                .lastName("Twain")
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(new AuthorResponseModel[]{author}),
                        MediaType.APPLICATION_JSON
                ));

        assertEquals(1, client.getAuthors().size());
    }

    @Test
    void testAddAuthor() {
        AuthorRequestModel request = AuthorRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess());

        client.addAuthor(request);
    }

    @Test
    void testUpdateAuthor() throws Exception {
        String id = UUID.randomUUID().toString();
        AuthorRequestModel request = AuthorRequestModel.builder().build();

        AuthorResponseModel responseModel = AuthorResponseModel.builder()
                .authorid(id)
                .firstName("Jane")
                .lastName("Austen")
                .pseudonym("N/A")
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(responseModel),
                        MediaType.APPLICATION_JSON
                ));

        AuthorResponseModel response = client.updateAuthor(id, request);
        assertEquals(id, response.getAuthorid());
    }

    @Test
    void testDeleteAuthor() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        client.deleteAuthor(id);
    }

    @Test
    void testAddAuthor_InvalidInput() {
        AuthorRequestModel invalidRequest = AuthorRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.addAuthor(invalidRequest));
    }

    @Test
    void testAddAuthor_Duplicate() {
        AuthorRequestModel duplicate = AuthorRequestModel.builder()
                .firstName("Jane")
                .lastName("Austen")
                .pseudonym(null)
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.addAuthor(duplicate));
    }

    @Test
    void testGetAuthorById_NotFound() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getAuthorByAuthorId(id));
    }
}
