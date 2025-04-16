package com.isaacwallace.author_service.Presentation;

import com.isaacwallace.author_service.DataAccess.AuthorRepository;
import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
@Sql({"classpath:/sql/author-service-data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthorControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthorRepository authorRepository;

    private final String SERVICE_URI_AUTHORS = "/api/v1/authors";

    private final String NOT_FOUND_AUTHOR_ID = "00000000-0000-0000-0000-00000000000";
    private final String INVALID_AUTHOR_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_AUTHOR_ID = "123e4567-e89b-12d3-a456-556642440000";

    @Test
    public void whenAuthorExists_thenReturnAllAuthors() {
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
    public void whenAuthorsExist_thenReturnNewAuthor() {
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
    public void whenAuthorExistsOnDelete_thenReturnNoContent() {
        this.webTestClient.delete()
                .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
                .exchange()
                .expectStatus().isNoContent();

        this.webTestClient.get()
                .uri(SERVICE_URI_AUTHORS + "/" + VALID_AUTHOR_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Author id not found: " + VALID_AUTHOR_ID);
    }

    @Test
    public void whenAuthorIdIsInvalid_thenReturnUnprocessableEntity() {
        this.webTestClient.delete()
                .uri(SERVICE_URI_AUTHORS + "/" + INVALID_AUTHOR_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Author id is invalid: " + INVALID_AUTHOR_ID);
    }

    @Test
    public void whenAuthorIdIsNotFound_thenReturnNotFound() {
        this.webTestClient.delete()
                .uri(SERVICE_URI_AUTHORS + "/" + NOT_FOUND_AUTHOR_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Author id not found: " + NOT_FOUND_AUTHOR_ID);
    }
}