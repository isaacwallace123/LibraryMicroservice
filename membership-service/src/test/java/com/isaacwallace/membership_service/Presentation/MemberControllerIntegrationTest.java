package com.isaacwallace.membership_service.Presentation;

import com.isaacwallace.membership_service.DataAccess.*;
import com.isaacwallace.membership_service.DomainClient.TransactionServiceClient;
import com.isaacwallace.membership_service.Presentation.Models.MemberRequestModel;
import com.isaacwallace.membership_service.Presentation.Models.MemberResponseModel;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data-psql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoBean
    private TransactionServiceClient transactionServiceClient;

    private final String SERVICE_URI = "/api/v1/members";

    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_ID = "123e4567-e89b-12d3-a456-556642440000";

    @BeforeEach
    void setup() {
        doNothing().when(transactionServiceClient).deleteTransactionByMemberId(anyString());
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
    void whenBulkMembersAreCreated_thenAllAreSavedCorrectly() {
        this.memberRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            MemberRequestModel model = MemberRequestModel.builder()
                    .firstName("First" + i)
                    .lastName("Last" + i)
                    .email("email" + i + "@email.com")
                    .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                    .address(new Address("123 Main St", "Anytown", "12345", "CA"))
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
                .expectBodyList(MemberResponseModel.class)
                .value(list -> assertTrue(list.size() >= 10));
    }

    @Test
    void getAllMembers_emptyDB_returnEmptyList() {
        this.memberRepository.deleteAll();

        this.webTestClient.get()
                .uri(SERVICE_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MemberResponseModel.class)
                .value(list -> assertTrue(list.isEmpty()));
    }

    /*--> RequestModel Tests <--*/

    @Test
    void testEqualsAndHashCodeOnRequestModel() {
        MemberRequestModel model1 = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        MemberRequestModel model2 = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        MemberRequestModel modelDifferent = MemberRequestModel.builder()
                .firstName("NotIsaac")
                .lastName("Wallace")
                .email("different@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
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
        MemberRequestModel model = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("isaac@example.com", model.getEmail());
        assertEquals("555-555-5555", model.getPhone().getNumber());
        assertEquals(PhoneType.MOBILE, model.getPhone().getType());
        assertEquals("123 Main St", model.getAddress().getStreet());
        assertEquals("Anytown", model.getAddress().getCity());
        assertEquals("12345", model.getAddress().getPostal());
        assertEquals("CA", model.getAddress().getProvince());
    }

    @Test
    void testRequestModelConstructor() {
        MemberRequestModel model = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("IsaacWallace@me.com", model.getEmail());
        assertEquals("555-555-5555", model.getPhone().getNumber());
        assertEquals(PhoneType.MOBILE, model.getPhone().getType());
        assertEquals("123 Main St", model.getAddress().getStreet());
        assertEquals("Anytown", model.getAddress().getCity());
        assertEquals("12345", model.getAddress().getPostal());
        assertEquals("CA", model.getAddress().getProvince());
    }

    @Test
    void testRequestModelEqualsAndHashCode() {
        MemberRequestModel model1 = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        MemberRequestModel model2 = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testRequestModelToString() {
        MemberRequestModel model = MemberRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertTrue(model.toString().contains("Isaac"));
        assertTrue(model.toString().contains("Wallace"));
        assertTrue(model.toString().contains("isaac@example.com"));
        assertTrue(model.toString().contains("555-555-5555"));
        assertTrue(model.toString().contains("MOBILE"));
        assertTrue(model.toString().contains("123 Main St"));
        assertTrue(model.toString().contains("Anytown"));
        assertTrue(model.toString().contains("12345"));
        assertTrue(model.toString().contains("CA"));
    }

    /*--> ResponseModel Tests <--*/

    @Test
    void testSetMemberIdOnResponseModel() {
        MemberResponseModel model = MemberResponseModel.builder().build();
        model.setMemberid("LOLTEST");
        assertEquals("LOLTEST", model.getMemberid());
    }

    @Test
    void testEqualsHashCode() {
        MemberResponseModel model1 = MemberResponseModel.builder().build();
        MemberResponseModel model2 = MemberResponseModel.builder().build();
        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());

        assertNotEquals(model1, null);
        assertNotEquals(model1, new Object());
        assertEquals(model1, model1);
    }

    @Test
    void testResponseModelConstructor() {
        MemberResponseModel model = MemberResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("IsaacWallace@me.com", model.getEmail());
        assertEquals("555-555-5555", model.getPhone().getNumber());
        assertEquals(PhoneType.MOBILE, model.getPhone().getType());
        assertEquals("123 Main St", model.getAddress().getStreet());
        assertEquals("Anytown", model.getAddress().getCity());
        assertEquals("12345", model.getAddress().getPostal());
        assertEquals("CA", model.getAddress().getProvince());
    }

    @Test
    void testResponseModelSettersAndGetters() {
        MemberResponseModel model = MemberResponseModel.builder().build();

        model.setFirstName("Isaac");
        model.setLastName("Wallace");
        model.setEmail("IsaacWallace@me.com");
        model.setPhone(new Phone("555-555-5555", PhoneType.MOBILE));
        model.setAddress(new Address("123 Main St", "Anytown", "12345", "CA"));

        assertEquals("Isaac", model.getFirstName());
        assertEquals("Wallace", model.getLastName());
        assertEquals("IsaacWallace@me.com", model.getEmail());
        assertEquals("555-555-5555", model.getPhone().getNumber());
        assertEquals(PhoneType.MOBILE, model.getPhone().getType());
        assertEquals("123 Main St", model.getAddress().getStreet());
        assertEquals("Anytown", model.getAddress().getCity());
        assertEquals("12345", model.getAddress().getPostal());
        assertEquals("CA", model.getAddress().getProvince());
    }

    @Test
    void testResponseModelEqualsAndHashCode() {
        MemberResponseModel model1 = MemberResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        MemberResponseModel model2 = MemberResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testResponseModelToString() {
        MemberResponseModel model = MemberResponseModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("IsaacWallace@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        assertTrue(model.toString().contains("Isaac"));
        assertTrue(model.toString().contains("Wallace"));
        assertTrue(model.toString().contains("IsaacWallace@me.com"));
        assertTrue(model.toString().contains("555-555-5555"));
        assertTrue(model.toString().contains("MOBILE"));
        assertTrue(model.toString().contains("123 Main St"));
        assertTrue(model.toString().contains("Anytown"));
        assertTrue(model.toString().contains("12345"));
        assertTrue(model.toString().contains("CA"));
    }

    /*--> CRUD Tests <--*/

    @Test
    void whenMemberExists_thenReturnAllMembers() {
        long sizeDB = this.memberRepository.count();

        this.webTestClient.get()
            .uri(SERVICE_URI)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(MemberResponseModel.class)
            .value((list) -> {
                assertNotNull(list);
                assertNotEquals(0, sizeDB);
                assertEquals(sizeDB, list.size());

                list.forEach((memberResponseModel) -> {
                    assertNotNull(memberResponseModel);
                    assertNotNull(memberResponseModel.getMemberid());
                    assertNotNull(memberResponseModel.getFirstName());
                    assertNotNull(memberResponseModel.getLastName());
                    assertNotNull(memberResponseModel.getEmail());
                    assertNotNull(memberResponseModel.getPhone());
                    assertNotNull(memberResponseModel.getAddress());
                });
            });
    }

    @Test
    void whenMemberIdExists_thenReturnIsOk() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(MemberResponseModel.class)
            .value((memberResponseModel) -> {
                assertNotNull(memberResponseModel);
                assertNotNull(memberResponseModel.getMemberid());
                assertNotNull(memberResponseModel.getFirstName());
                assertNotNull(memberResponseModel.getLastName());
                assertNotNull(memberResponseModel.getEmail());
                assertNotNull(memberResponseModel.getPhone());
                assertNotNull(memberResponseModel.getAddress());

                assertEquals(VALID_ID, memberResponseModel.getMemberid());
            });
    }

    @Test
    void whenMemberExistsOnCreate_thenReturnIsCreated() {
        MemberRequestModel model = MemberRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .email("IsaacWallace@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void whenMemberExistsOnUpdate_thenReturnIsOk() {
        MemberRequestModel model = MemberRequestModel.builder()
            .firstName("Isaac")
            .lastName("Wallace")
            .email("IsaacWallace@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(model)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenMemberExistsOnDelete_thenReturnIsNoContent() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .exchange()
            .expectStatus().isNoContent();
    }

    /*--> GET Tests <--*/

    @Test
    void whenMemberIdIsNotFoundOnGet_thenReturnNotFound() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown memberid: " + NOT_FOUND_ID);
    }

    @Test
    void whenMemberIdIsInvalidOnGet_thenReturnUnprocessableEntity() {
        this.webTestClient.get()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid memberid: " +  INVALID_ID);
    }

    /*--> POST Tests <--*/

    @Test
    void whenMemberLastNameIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName(null)
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
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
    void whenMemberFirstNameIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName(null)
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
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
    void whenMemberEmailIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email(null)
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid email: null");
    }

    @Test
    void whenMemberPhoneIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(null)
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid phone: null");
    }

    @Test
    void whenMemberPhoneTypeIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", null))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid phone type: null");
    }

    @Test
    void whenMemberPhoneNumberIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone(null, PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.post()
            .uri(SERVICE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid phone number: null");
    }

    @Test
    void whenMemberAddressIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(null)
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid address: null");
    }

    @Test
    void whenMemberAddressStreetIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address(null, "Anytown", "12345", "CA"))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid street: null");
    }

    @Test
    void whenMemberAddressCityIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", null, "12345", "CA"))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid city: null");
    }

    @Test
    void whenMemberAddressProvinceIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", null, "12345", "CA"))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid city: null");
    }

    @Test
    void whenMemberAddressZipIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", null, "CA"))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid zip: null");
    }

    @Test
    void whenMemberAddressCountryIsNullOnPost_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", null))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid province: null");
    }

    @Test
    void whenMemberFirstNameAndLastNameAlreadyExistsOnPost_thenReturnConflict() {
        Member member = new Member("John", "Doe", "l7V0o@example.com", new Address("123 Main St", "Anytown", "12345", "CA"), new Phone("555-555-5555", PhoneType.MOBILE));

        this.memberRepository.save(member);

        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@me.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Duplicate member: " + member.getFirstName() + " " + member.getLastName());
    }

    @Test
    void whenMemberEmailAlreadyExistsOnPost_thenReturnConflict() {
        Member member = new Member("John", "Doe", "l7V0o@example.com", new Address("123 Main St", "Anytown", "12345", "CA"), new Phone("555-555-5555", PhoneType.MOBILE));

        this.memberRepository.save(member);

        MemberRequestModel requestModel = MemberRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .email(member.getEmail())
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "Anytown", "12345", "CA"))
                .build();

        this.webTestClient.post()
                .uri(SERVICE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Duplicate member: " + member.getFirstName() + " " + member.getLastName());
    }

    /*--> PUT Tests <--*/

    @Test
    void whenMemberIdDoesNotExistOnPut_thenReturnNotFound() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown memberid: " + NOT_FOUND_ID);
    }

    @Test
    void whenMemberIdIsInvalidOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid memberid: " + INVALID_ID);
    }

    @Test
    void whenMemberFirstNameIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName(null)
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid firstName: null");
    }

    @Test
    void whenMemberLastNameIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName(null)
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid lastName: null");
    }

    @Test
    void whenMemberEmailIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email(null)
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid email: null");
    }

    @Test
    void whenMemberPhoneIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(null)
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid phone: null");
    }

    @Test
    void whenMemberPhoneTypeIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", null))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid phone type: null");
    }

    @Test
    void whenMemberPhoneNumberIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone(null, PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid phone number: null");
    }

    @Test
    void whenMemberAddressIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(null)
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid address: null");
    }

    @Test
    void whenMemberAddressStreetIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address(null, "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid street: null");
    }

    @Test
    void whenMemberAddressCityIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", null, "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid city: null");
    }

    @Test
    void whenMemberAddressPostalCodeIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", null, "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid zip: null");
    }

    @Test
    void whenMemberAddressProvinceIsNullOnPut_thenReturnUnprocessableEntity() {
        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Montreal", "12345", null))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid province: null");
    }

    @Test
    void whenMemberAlreadyExistsOnPut_thenReturnConflict() {
        Member member = new Member("John", "Doe", "l7V0o@example.com", new Address("123 Main St", "Anytown", "12345", "CA"), new Phone("555-555-5555", PhoneType.MOBILE));

        this.memberRepository.save(member);

        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("John")
            .lastName("Doe")
            .email("JohnDoe@me.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Duplicate member: " + requestModel.getFirstName() + " " + requestModel.getLastName());
    }

    @Test
    void whenMemberEmailAlreadyExistsOnPut_thenReturnConflict() {
        Member member = new Member("John", "Doe", "l7V0o@example.com", new Address("123 Main St", "Anytown", "12345", "CA"), new Phone("555-555-5555", PhoneType.MOBILE));

        this.memberRepository.save(member);

        MemberRequestModel requestModel = MemberRequestModel.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("l7V0o@example.com")
            .phone(new Phone("555-555-5555", PhoneType.MOBILE))
            .address(new Address("123 Main St", "Anytown", "12345", "CA"))
            .build();

        this.webTestClient.put()
            .uri(SERVICE_URI + "/" + VALID_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestModel)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Duplicate email: " + requestModel.getEmail());
    }

    /*--> DELETE Tests <--*/

    @Test
    void whenMemberIdDoesNotExistOnDelete_thenReturnNotFound() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + NOT_FOUND_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Unknown memberid: " + NOT_FOUND_ID);
    }

    @Test
    void whenMemberIdIsInvalidOnDelete_thenReturnUnprocessableEntity() {
        this.webTestClient.delete()
            .uri(SERVICE_URI + "/" + INVALID_ID)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Invalid memberid: " +  INVALID_ID);
    }
}