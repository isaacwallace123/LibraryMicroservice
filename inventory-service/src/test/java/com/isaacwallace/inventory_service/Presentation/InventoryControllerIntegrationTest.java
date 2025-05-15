package com.isaacwallace.inventory_service.Presentation;

import com.isaacwallace.inventory_service.DataAccess.BookRepository;
import com.isaacwallace.inventory_service.DomainClient.Author.AuthorServiceClient;
import com.isaacwallace.inventory_service.DomainClient.Transaction.TransactionServiceClient;
import com.isaacwallace.inventory_service.Presentation.Models.BookRequestModel;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data-psql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InventoryControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    @MockitoBean
    private TransactionServiceClient transactionServiceClient;

    @MockitoBean
    private AuthorServiceClient authorServiceClient;

    private final String SERVICE_URI = "/api/v1/inventory";

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_ID = "c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5";

    @BeforeEach
    void setup() {
        doNothing().when(transactionServiceClient).deleteTransactionByInventoryId(anyString());
    }

    /*--> Header Tests <--*/

    @Test
    void whenUnsupportedMediaTypeRequested_thenReturnNotAcceptable() {
        this.webTestClient.get()
                .uri(SERVICE_URI)
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    /*--> Validation Tests <--*/

    @Test
    void whenBulkInventoryIsCreated_thenAllAreSavedCorrectly() {
        this.bookRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            BookRequestModel model = BookRequestModel.builder()
                    .authorid("123e4567-e89b-12d3-a456-556642440000")
                    .title("Title" + i)
                    .genre("Genre" + i)
                    .publisher("Publisher" + i)
                    .released(LocalDateTime.now())
                    .stock(10)
                    .build();

            this.webTestClient.post()
                    .uri(SERVICE_URI)
                    .bodyValue(model)
                    .exchange()
                    .expectStatus().isCreated();
        }

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookResponseModel.class)
                .value(list -> assertTrue(list.size() >= 10));
    }

    @Test
    void getAllMembers_emptyDB_returnEmptyList() {
        this.bookRepository.deleteAll();

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookResponseModel.class)
                .value(list -> assertTrue(list.isEmpty()));
    }

    /*--> RequestModel Tests <--*/

    @Test
    void testEqualsAndHashCodeOnRequestModel() {
        LocalDateTime now = LocalDateTime.now();

        BookRequestModel model1 = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        BookRequestModel model2 = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        BookRequestModel modelDifferent = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("DifferentTitle")
                .genre("Genre")
                .publisher("Publisher")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        assertEquals(model1, model2);
        assertNotEquals(model1, modelDifferent);
        assertNotEquals(model2, modelDifferent);

        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), modelDifferent.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
    }

    @Test
    void testGettersOnRequestModel() {
        LocalDateTime now = LocalDateTime.now();

        BookRequestModel model = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertEquals("123e4567-e89b-12d3-a456-556642440000", model.getAuthorid());
        assertEquals("Title", model.getTitle());
        assertEquals("Genre", model.getGenre());
        assertEquals("Publisher", model.getPublisher());
        assertEquals(now, model.getReleased());
        assertEquals(10, model.getStock());
    }

    @Test
    void testRequestModelConstructor() {
        LocalDateTime now = LocalDateTime.now();

        BookRequestModel model = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertEquals("123e4567-e89b-12d3-a456-556642440000", model.getAuthorid());
        assertEquals("Title", model.getTitle());
        assertEquals("Genre", model.getGenre());
        assertEquals("Publisher", model.getPublisher());
        assertEquals(now, model.getReleased());
        assertEquals(10, model.getStock());
    }

    @Test
    void testRequestModelEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        BookRequestModel model1 = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        BookRequestModel model2 = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testRequestModelToString() {
        LocalDateTime now = LocalDateTime.now();

        BookRequestModel model = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertTrue(model.toString().contains("123e4567-e89b-12d3-a456-556642440000"));
        assertTrue(model.toString().contains("Title"));
        assertTrue(model.toString().contains("Genre"));
        assertTrue(model.toString().contains("Publisher"));
        assertTrue(model.toString().contains(now.toString()));
        assertTrue(model.toString().contains("10"));
    }

    /*--> ResponseModel Tests <--*/

    @Test
    void testSetBookIdOnResponseModel() {
        BookResponseModel model = BookResponseModel.builder().build();
        model.setBookid("LOLTEST");
        assertEquals("LOLTEST", model.getBookid());
    }

    @Test
    void testEqualsHashCode() {
        BookResponseModel model1 = BookResponseModel.builder().build();
        BookResponseModel model2 = BookResponseModel.builder().build();
        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
        assertEquals(model1, model1);
    }

    @Test
    void testResponseModelConstructor() {
        LocalDateTime now = LocalDateTime.now();

        BookResponseModel model = BookResponseModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertEquals("123e4567-e89b-12d3-a456-556642440000", model.getAuthorid());
        assertEquals("Title", model.getTitle());
        assertEquals("Genre", model.getGenre());
        assertEquals("Publisher", model.getPublisher());
        assertEquals(now, model.getReleased());
        assertEquals(10, model.getStock());
    }

    @Test
    void testResponseModelSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        BookResponseModel model = BookResponseModel.builder().build();

        model.setBookid("123e4567-e89b-12d3-a456-556642440000");
        model.setTitle("Title");
        model.setGenre("Genre");
        model.setPublisher("Publisher");
        model.setReleased(now);
        model.setStock(10);

        assertEquals("123e4567-e89b-12d3-a456-556642440000", model.getBookid());
        assertEquals("Title", model.getTitle());
        assertEquals("Genre", model.getGenre());
        assertEquals("Publisher", model.getPublisher());
        assertEquals(now, model.getReleased());
        assertEquals(10, model.getStock());
    }

    @Test
    void testResponseModelEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        BookResponseModel model1 = BookResponseModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        BookResponseModel model2 = BookResponseModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testResponseModelToString() {
        LocalDateTime now = LocalDateTime.now();

        BookResponseModel model = BookResponseModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(now)
                .stock(10)
                .build();

        assertTrue(model.toString().contains("123e4567-e89b-12d3-a456-556642440000"));
        assertTrue(model.toString().contains("Title"));
        assertTrue(model.toString().contains("Genre"));
        assertTrue(model.toString().contains("Publisher"));
        assertTrue(model.toString().contains(now.toString()));
        assertTrue(model.toString().contains("10"));
    }

    /*--> CRUD Tests <--*/

    @Test
    void whenInventoryExists_thenReturnAllBooks() {
        long sizeDB = this.bookRepository.count();

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookResponseModel.class)
                .value(list -> {
                    assertNotNull(list);
                    assertNotEquals(0, sizeDB);
                    assertEquals(sizeDB, list.size());

                    list.forEach(bookResponseModel -> {
                        assertNotNull(bookResponseModel);
                        assertNotNull(bookResponseModel.getBookid());
                        assertNotNull(bookResponseModel.getTitle());
                        assertNotNull(bookResponseModel.getGenre());
                        assertNotNull(bookResponseModel.getPublisher());
                        assertNotNull(bookResponseModel.getReleased());
                        assertNotNull(bookResponseModel.getStock());
                    });
                });
    }

    @Test
    void whenInventoryIdExists_thenReturnIsOk() {
        this.webTestClient.get()
                .uri(SERVICE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookResponseModel.class)
                .value(bookResponseModel -> {
                    assertNotNull(bookResponseModel);
                    assertNotNull(bookResponseModel.getBookid());
                    assertNotNull(bookResponseModel.getTitle());
                    assertNotNull(bookResponseModel.getGenre());
                    assertNotNull(bookResponseModel.getPublisher());
                    assertNotNull(bookResponseModel.getReleased());
                    assertNotNull(bookResponseModel.getStock());

                    assertEquals(VALID_ID, bookResponseModel.getBookid());
                });
    }

    @Test
    void whenInventoryExistsOnCreate_thenReturnIsCreated() {
        BookRequestModel model = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void whenInventoryExistsOnUpdate_thenReturnIsOk() {
        BookRequestModel model = BookRequestModel.builder()
                .authorid("123e4567-e89b-12d3-a456-556642440000")
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        this.webTestClient.put()
                .uri(SERVICE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenInventoryExistsOnDelete_thenReturnIsNoContent() {
        this.webTestClient.delete()
                .uri(SERVICE_URI + "/" + VALID_ID)
                .exchange()
                .expectStatus().isNoContent();
    }

    /*--> GET Tests <--*/

    @Test
    void whenInventoryIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
                .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown bookid: " + NOT_FOUND_ID);
    }

    @Test
    void whenInventoryIdIsInvalidOnGet_thenReturnUnprocessableEntity() {
        this.webTestClient.get()
                .uri(SERVICE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid bookid: " + INVALID_ID);
    }

    /*--> POST Tests <--*/

    @Test
    void whenAuthorIdIsInvalidOnCreate_thenReturnUnprocessableEntity() {
        BookRequestModel model = BookRequestModel.builder()
                .title("Title")
                .genre("Genre")
                .publisher("Publisher")
                .released(LocalDateTime.now())
                .stock(10)
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Book must be associated with an existing author.");
    }

    /*--> DELETE Tests <--*/

    @Test
    void whenInventoryIdDoesNotExistOnDelete_thenReturnNotFound() {
        this.webTestClient.delete()
                .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Unknown bookid: " + NOT_FOUND_ID);
    }

    @Test
    void whenInventoryIdIsInvalidOnDelete_thenReturnUnprocessableEntity() {
        this.webTestClient.delete()
                .uri(SERVICE_URI + "/" + INVALID_ID)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid bookid: " + INVALID_ID);
    }
}