package com.isaacwallace.transaction_service.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isaacwallace.transaction_service.DomainClient.Membership.MembershipServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.Address;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.MemberResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.Phone;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.PhoneType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("test")
public class MembershipServiceClientTest {
    private final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";
    private final String INVALID_ID = "00000000-0000-0000-0000-0000000000000";
    private final String VALID_MEMBER_ID = "823e4567-e89b-12d3-a456-556642440007";

    private final String BASE_URL = "http://localhost:8080/api/v1/members";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private MembershipServiceClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        this.server = MockRestServiceServer.createServer(restTemplate);
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.client = new MembershipServiceClient(restTemplate, objectMapper, "localhost", "8080");
    }

    @Test
    void testMemberResponseModelEquality() {
        MemberResponseModel model1 = MemberResponseModel.builder()
                .memberid("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "City", "12345", "CA"))
                .build();

        MemberResponseModel model2 = MemberResponseModel.builder()
                .memberid("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "City", "12345", "CA"))
                .build();

        assertEquals(model1, model2);
    }

    @Test
    void testMemberResponseModelHashCode() {
        MemberResponseModel model1 = MemberResponseModel.builder()
                .memberid("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "City", "12345", "CA"))
                .build();

        MemberResponseModel model2 = MemberResponseModel.builder()
                .memberid("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "City", "12345", "CA"))
                .build();

        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testMemberResponseModelToString() {
        MemberResponseModel model = MemberResponseModel.builder()
                .memberid("123")
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "City", "12345", "CA"))
                .build();

        String stringOutput = model.toString();
        assertTrue(stringOutput.contains("Isaac"));
        assertTrue(stringOutput.contains("123"));
        assertTrue(stringOutput.contains("Wallace"));
        assertTrue(stringOutput.contains("isaac@example.com"));
        assertTrue(stringOutput.contains("555-555-5555"));
        assertTrue(stringOutput.contains("MOBILE"));
        assertTrue(stringOutput.contains("123 Main St"));
        assertTrue(stringOutput.contains("City"));
        assertTrue(stringOutput.contains("12345"));
        assertTrue(stringOutput.contains("CA"));
    }

    @Test
    void testGetMemberById_fromServiceClient() throws Exception {
        MemberResponseModel mockMember = MemberResponseModel.builder()
                .memberid(VALID_MEMBER_ID)
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-555-5555", PhoneType.MOBILE))
                .address(new Address("123 Main St", "City", "12345", "CA"))
                .build();

        String json = objectMapper.writeValueAsString(mockMember);

        server.expect(requestTo(BASE_URL + "/" + VALID_MEMBER_ID))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        MemberResponseModel response = client.getMemberById(VALID_MEMBER_ID);

        assertNotNull(response);
        assertEquals("Isaac", response.getFirstName());
        assertEquals(VALID_MEMBER_ID, response.getMemberid());

        server.verify();
    }

    @Test
    void whenMemberNotFound_thenThrowNotFoundException() {
        server.expect(requestTo(BASE_URL + "/" + NOT_FOUND_ID))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getMemberById(NOT_FOUND_ID));
    }

    @Test
    void whenMemberIdIsInvalid_thenThrowInvalidInputException() {
        server.expect(requestTo(BASE_URL + "/" + INVALID_ID))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.getMemberById(INVALID_ID));
    }

    @Test
    void whenConflictOccurs_thenThrowDuplicateResourceException() {
        server.expect(requestTo(BASE_URL + "/" + VALID_MEMBER_ID))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.getMemberById(VALID_MEMBER_ID));
    }
}