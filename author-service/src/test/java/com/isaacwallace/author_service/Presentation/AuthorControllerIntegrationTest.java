package com.isaacwallace.author_service.Presentation;

import com.isaacwallace.author_service.DataAccess.AuthorRepository;
import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthorControllerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(AuthorControllerIntegrationTest.class);
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthorRepository authorRepository;

    private final String SERVICE_URI_AUTHORS = "/api/v1/authors";

    private final String NOT_FOUND_AUTHOR_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_AUTHOR_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_AUTHOR_ID = "123e4567-e89b-12d3-a456-556642440000";

    /*--> Validation tests <--*/

    @Test
    void whenAuthorExists_thenReturnAllAuthors() {
        long sizeDB = this.authorRepository.count();

        this.webTestClient.get()
            .uri(SERVICE_URI_AUTHORS)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(AuthorResponseModel.class)
            .value((list) -> {
                assertNotNull(list);
                assertNotEquals(0, sizeDB);
                assertEquals(sizeDB, list.size());
            });
    }

    @Test
    void whenAuthorIdExists_thenReturnIsOk() {
        this.webTestClient.get()
            .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthorResponseModel.class)
            .value((authorResponseModel) -> {
                assertNotNull(authorResponseModel);
                assertNotNull(authorResponseModel.getAuthorId());
                assertNotNull(authorResponseModel.getFirstName());
                assertNotNull(authorResponseModel.getLastName());
            });
    }

    @Test
    void whenAuthorsExist_thenReturnNewAuthor() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI_AUTHORS)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthorResponseModel.class)
            .value((authorResponseModel) -> {
                assertNotNull(authorResponseModel);
                assertNotNull(authorResponseModel.getAuthorId());
                assertNotNull(authorResponseModel.getFirstName());
                assertNotNull(authorResponseModel.getLastName());
                assertNotNull(authorResponseModel.getPseudonym());

                assertEquals(authorRequestModel.getFirstName(), authorResponseModel.getFirstName());
                assertEquals(authorRequestModel.getLastName(), authorResponseModel.getLastName());
                assertEquals(authorRequestModel.getPseudonym(), authorResponseModel.getPseudonym());
            });
    }

    @Test
    void whenAuthorExistsOnUpdate_thenReturnIsOk() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthorResponseModel.class)
            .value((authorResponseModel) -> {
                assertNotNull(authorResponseModel);
                assertNotNull(authorResponseModel.getAuthorId());
                assertNotNull(authorResponseModel.getFirstName());
                assertNotNull(authorResponseModel.getLastName());

                assertEquals(authorRequestModel.getFirstName(), authorResponseModel.getFirstName());
                assertEquals(authorRequestModel.getLastName(), authorResponseModel.getLastName());
                assertEquals(authorRequestModel.getPseudonym(), authorResponseModel.getPseudonym());
            });
    }

    @Test
    void whenAuthorExistsOnDelete_thenReturnNoContent() {
        this.webTestClient.delete()
            .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
            .exchange()
            .expectStatus().isNoContent();

        this.webTestClient.get()
            .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + VALID_AUTHOR_ID);
    }

    /*--> GET Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnGet_thenReturnBadRequest() {
        this.webTestClient.get()
            .uri(SERVICE_URI_AUTHORS + "/" + INVALID_AUTHOR_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid authorid: " +  INVALID_AUTHOR_ID);
    }

    @Test
    void whenAuthorIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
            .uri(SERVICE_URI_AUTHORS + "/" + NOT_FOUND_AUTHOR_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + NOT_FOUND_AUTHOR_ID);
    }

    /*--> POST Tests <--*/

    @Test
    void whenAuthorFirstNameIsInvalidOnPost_thenReturnBadRequest() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI_AUTHORS)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: " + authorRequestModel.getFirstName());
    }

    @Test
    void whenAuthorLastNameIsInvalidOnPost_thenReturnBadRequest() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("")
            .pseudonym("Test")
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI_AUTHORS)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: " + authorRequestModel.getLastName());
    }

    /*--> PUT Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnPut_thenReturnBadRequest() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI_AUTHORS + "/" + INVALID_AUTHOR_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid authorid: " +  INVALID_AUTHOR_ID);
    }

    @Test
    void whenAuthorIdIsNotFoundOnPut_thenReturnNotFound() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI_AUTHORS + "/" + NOT_FOUND_AUTHOR_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + NOT_FOUND_AUTHOR_ID);
    }

    @Test
    void whenAuthorFirstNameIsInvalidOnPut_thenReturnBadRequest() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: " + authorRequestModel.getFirstName());
    }

    @Test
    void whenAuthorLastNameIsInvalidOnPut_thenReturnBadRequest() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: " + authorRequestModel.getLastName());
    }

    /*--> DELETE Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnDelete_thenReturnBadRequest() {
        this.webTestClient.delete()
            .uri(SERVICE_URI_AUTHORS + "/" + INVALID_AUTHOR_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid authorid: " +  INVALID_AUTHOR_ID);
    }

    @Test
    void whenAuthorIdIsNotFoundOnDelete_thenReturnNotFound() {
        this.webTestClient.delete()
            .uri(SERVICE_URI_AUTHORS + "/" + NOT_FOUND_AUTHOR_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + NOT_FOUND_AUTHOR_ID);
    }

    /**--> Database Tests --*/

    @Test
    void getAllAccounts_emptyDB_returnEmptyList() {
        this.authorRepository.deleteAll();

        this.webTestClient.get()
            .uri(SERVICE_URI_AUTHORS)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$").isEmpty();
    }
}