package com.isaacwallace.author_service.Presentation;

import com.isaacwallace.author_service.DataAccess.AuthorRepository;
import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import org.junit.jupiter.api.Test;
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
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthorRepository authorRepository;

    private final String SERVICE_URI = "/api/v1/authors";

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_ID = "123e4567-e89b-12d3-a456-556642440000";

    /*--> Header Tests <--*/

    @Test
    void whenUnsupportedMediaTypeRequested_thenReturnNotAcceptable() {
        this.webTestClient.get()
            .uri(SERVICE_URI)
            .accept(MediaType.APPLICATION_XML)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    /*--> RequestModel Tests <--*/

    @Test
    void testRequestModelConstructor() {
        AuthorRequestModel model = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("Test", model.getPseudonym());
    }

    @Test
    void testRequestModelEqualsAndHashCode() {
        AuthorRequestModel a = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        AuthorRequestModel b = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testRequestModelToString() {
        AuthorRequestModel model = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        assertTrue(model.toString().contains("Isaac"));
        assertTrue(model.toString().contains("Wallace"));
        assertTrue(model.toString().contains("Test"));
    }

    /*--> ResponseModel Tests <--*/

    @Test
    void testResponseModelContructor() {
        AuthorResponseModel model = AuthorResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("Test", model.getPseudonym());
    }

    @Test
    void testResponseModelSettersAndGetters() {
        AuthorResponseModel model = AuthorResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        model.setFirstName("Test");
        model.setLastName("Boob");
        model.setPseudonym("Different");

        assertEquals("Test", model.getFirstName());
        assertEquals("Boob", model.getLastName());
        assertEquals("Different", model.getPseudonym());
    }

    @Test
    void testResponseModelEqualsAndHashCode() {
        AuthorResponseModel a = AuthorResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        AuthorResponseModel b = AuthorResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testResponseModelToString() {
        AuthorResponseModel model = AuthorResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("Test")
                .build();

        assertTrue(model.toString().contains("Isaac"));
        assertTrue(model.toString().contains("Wallace"));
        assertTrue(model.toString().contains("Test"));
    }

    /*--> Validation Tests <--*/

    @Test
    void whenBulkAuthorsAreCreated_thenAllAreSavedCorrectly() {
        this.authorRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            AuthorRequestModel model = AuthorRequestModel.builder()
                .firstName("First" + i)
                .lastName("Last" + i)
                .pseudonym("Pseudonym" + i)
                .build();

            this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated();
        }

        this.webTestClient.get()
            .uri(SERVICE_URI)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(AuthorResponseModel.class)
            .value(list -> assertTrue(list.size() >= 10));
    }

    @Test
    void getAllAuthors_emptyDB_returnEmptyList() {
        this.authorRepository.deleteAll();

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }

    /*--> CRUD Tests <--*/

    @Test
    void whenAuthorExists_thenReturnAllAuthors() {
        long sizeDB = this.authorRepository.count();

        this.webTestClient.get()
            .uri(SERVICE_URI)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(AuthorResponseModel.class)
            .value((list) -> {
                assertNotNull(list);
                assertNotEquals(0, sizeDB);
                assertEquals(sizeDB, list.size());

                list.forEach((authorResponseModel) -> {
                    assertNotNull(authorResponseModel);
                    assertNotNull(authorResponseModel.getAuthorId());
                    assertNotNull(authorResponseModel.getFirstName());
                    assertNotNull(authorResponseModel.getLastName());
                });
            });
    }

    @Test
    void whenAuthorIdExists_thenReturnIsOk() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(AuthorResponseModel.class)
            .value((authorResponseModel) -> {
                assertNotNull(authorResponseModel);
                assertNotNull(authorResponseModel.getAuthorId());
                assertNotNull(authorResponseModel.getFirstName());
                assertNotNull(authorResponseModel.getLastName());

                assertEquals(VALID_ID, authorResponseModel.getAuthorId());
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
            .uri(SERVICE_URI)
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
            .uri(SERVICE_URI + "/" + VALID_ID)
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

                assertEquals(VALID_ID, authorResponseModel.getAuthorId());
            });
    }

    @Test
    void whenAuthorExistsOnDelete_thenReturnNoContent() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isNoContent();

        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + VALID_ID);
    }

    /*--> GET Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnGet_thenReturnUnprocessableEntity() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid authorid: " +  INVALID_ID);
    }

    @Test
    void whenAuthorIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + NOT_FOUND_ID);
    }

    /*--> POST Tests <--*/

    @Test
    void whenAuthorFirstNameIsInvalidOnPost_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: " + authorRequestModel.getFirstName());
    }

    @Test
    void whenAuthorLastNameIsInvalidOnPost_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("")
            .pseudonym("Test")
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: " + authorRequestModel.getLastName());
    }

    @Test
    void whenAuthorFirstNameIsNullOnPost_thenReturnUnprocessableEntity() {
        AuthorRequestModel requestModel = AuthorRequestModel.builder()
                .firstName(null)
                .lastName("Doe")
                .pseudonym("JD")
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid firstName: null");
    }

    @Test
    void whenAuthorLastNameIsNullOnPost_thenReturnUnprocessableEntity() {
        AuthorRequestModel requestModel = AuthorRequestModel.builder()
                .firstName("John")
                .lastName(null)
                .pseudonym("JD")
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid lastName: null");
    }

    @Test
    void whenAuthorAlreadyExistsOnPost_thenReturnConflict() {
        AuthorRequestModel existingAuthor = AuthorRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .pseudonym("JD")
            .build();

        // First request: should succeed
        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(existingAuthor)
            .exchange()
            .expectStatus().isCreated();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(existingAuthor)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    /*--> PUT Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnPut_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid authorid: " +  INVALID_ID);
    }

    @Test
    void whenAuthorIdIsNotFoundOnPut_thenReturnNotFound() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + NOT_FOUND_ID);
    }

    @Test
    void whenAuthorFirstNameIsInvalidOnPut_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("")
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: " + authorRequestModel.getFirstName());
    }

    @Test
    void whenAuthorLastNameIsInvalidOnPut_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName("")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: " + authorRequestModel.getLastName());
    }

    @Test
    void whenAuthorFirstNameIsNullOnPut_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName(null)
            .lastName("Wallace")
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: null");
    }

    @Test
    void whenAuthorLastNameIsNullOnPut_thenReturnUnprocessableEntity() {
        AuthorRequestModel authorRequestModel = AuthorRequestModel.builder()
            .firstName("Isaac")
            .lastName(null)
            .pseudonym("Test")
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(authorRequestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: null");
    }

    @Test
    void whenAuthorAlreadyExistsOnPut_thenReturnConflict() {
        AuthorRequestModel existingAuthor = AuthorRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .pseudonym("JD")
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(existingAuthor)
            .exchange()
            .expectStatus().isCreated();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(existingAuthor)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    /*--> DELETE Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnDelete_thenReturnUnprocessableEntity() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid authorid: " +  INVALID_ID);
    }

    @Test
    void whenAuthorIdIsNotFoundOnDelete_thenReturnNotFound() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown authorid: " + NOT_FOUND_ID);
    }

    /*--> Model Tests <--*/

    @Test
    void testAuthorResponseModelBuilderAndGetters() {
        AuthorResponseModel response = AuthorResponseModel.builder()
                .authorId("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("I.W.")
                .build();

        assertEquals("123", response.getAuthorId());
        assertEquals("Isaac", response.getFirstName());
        assertEquals("Wallace", response.getLastName());
        assertEquals("I.W.", response.getPseudonym());

        String toString = response.toString();
        assertTrue(toString.contains("Isaac"));
        assertTrue(toString.contains("123"));

        AuthorResponseModel other = AuthorResponseModel.builder()
                .authorId("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("I.W.")
                .build();

        assertEquals(response, other);
        assertEquals(response.hashCode(), other.hashCode());
    }

    @Test
    void testAuthorRequestModelBuilderAndGetters() {
        AuthorRequestModel request = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("I.W.")
                .build();

        assertEquals("Isaac", request.getFirstName());
        assertEquals("Wallace", request.getLastName());
        assertEquals("I.W.", request.getPseudonym());
    }

    @Test
    void testAuthorRequestModelBuilderAndEquality() {
        AuthorRequestModel author1 = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("I.W.")
                .build();

        AuthorRequestModel author2 = AuthorRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .pseudonym("I.W.")
                .build();

        assertEquals(author1, author2);
        assertEquals(author1.hashCode(), author2.hashCode());
        assertTrue(author1.toString().contains("Isaac"));
    }
}