
package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipResponseModel;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.Address;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.Phone;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.PhoneType;
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
public class MembershipServiceClientTest {
    private final String BASE_URI = "http://localhost:8080/api/v1/members";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private MembershipServiceClient client;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.client = new MembershipServiceClient(restTemplate, new ObjectMapper(), "localhost", "8080");
    }

    @Test
    void testGetMemberById() throws Exception {
        String id = UUID.randomUUID().toString();
        MembershipResponseModel mockResponse = MembershipResponseModel.builder()
                .memberid(id)
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-1234", PhoneType.MOBILE))
                .address(new Address("123 St", "City", "11111", "CA"))
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        MembershipResponseModel response = client.getMemberByMemberId(id);
        assertEquals(id, response.getMemberid());
    }

    @Test
    void testGetAllMembers() throws Exception {
        MembershipResponseModel member = MembershipResponseModel.builder()
                .memberid(UUID.randomUUID().toString())
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-1234", PhoneType.MOBILE))
                .address(new Address("123 St", "City", "11111", "CA"))
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(new MembershipResponseModel[]{member}),
                        MediaType.APPLICATION_JSON
                ));

        assertEquals(1, client.getMembers().size());
    }

    @Test
    void testAddMember() {
        MembershipRequestModel request = MembershipRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess());

        client.addMember(request);
    }

    @Test
    void testUpdateMember() throws Exception {
        String id = UUID.randomUUID().toString();
        MembershipRequestModel request = MembershipRequestModel.builder().build();

        MembershipResponseModel responseModel = MembershipResponseModel.builder()
                .memberid(id)
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-1234", PhoneType.MOBILE))
                .address(new Address("123 St", "City", "11111", "CA"))
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(responseModel),
                        MediaType.APPLICATION_JSON
                ));

        MembershipResponseModel response = client.updateMember(id, request);
        assertEquals(id, response.getMemberid());
    }

    @Test
    void testDeleteMember() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        client.deleteMember(id);
    }

    @Test
    void testAddMember_InvalidInput() {
        MembershipRequestModel invalidRequest = MembershipRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.addMember(invalidRequest));
    }

    @Test
    void testAddMember_Duplicate() {
        MembershipRequestModel duplicate = MembershipRequestModel.builder()
                .firstName("Isaac")
                .lastName("Wallace")
                .email("isaac@example.com")
                .phone(new Phone("555-1234", PhoneType.MOBILE))
                .address(new Address("123 St", "City", "11111", "CA"))
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.addMember(duplicate));
    }

    @Test
    void testGetMemberById_NotFound() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getMemberByMemberId(id));
    }
}
