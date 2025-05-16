package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryResponseModel;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
@ActiveProfiles("test")
public class InventoryServiceClientTest {

    private final String BASE_URI = "http://localhost:8080/api/v1/inventory";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private InventoryServiceClient client;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.client = new InventoryServiceClient(restTemplate, new ObjectMapper().registerModule(new JavaTimeModule()), "localhost", "8080");
    }

    @Test
    void testGetInventoryById() throws Exception {
        String id = UUID.randomUUID().toString();
        InventoryResponseModel responseModel = InventoryResponseModel.builder()
                .bookid(id)
                .title("Sample Book")
                .genre("Sci-Fi")
                .publisher("Sample Pub")
                .released(LocalDateTime.now())
                .stock(5)
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(responseModel), MediaType.APPLICATION_JSON));

        InventoryResponseModel response = client.getInventoryByInventoryId(id);
        assertEquals(id, response.getBookid());
    }

    @Test
    void testGetAllInventorys() throws Exception {
        InventoryResponseModel model = InventoryResponseModel.builder()
                .bookid(UUID.randomUUID().toString())
                .title("Title")
                .genre("Fantasy")
                .publisher("Test Publisher")
                .released(LocalDateTime.now())
                .stock(3)
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess(
                        new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(new InventoryResponseModel[]{model}),
                        MediaType.APPLICATION_JSON
                ));

        assertEquals(1, client.getInventorys().size());
    }

    @Test
    void testAddInventory() {
        InventoryRequestModel request = InventoryRequestModel.builder()
                .authorid(UUID.randomUUID().toString())
                .title("Title")
                .genre("Drama")
                .publisher("Pub")
                .released(LocalDateTime.now())
                .stock(8)
                .build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withSuccess());

        client.addInventory(request);
    }

    @Test
    void testUpdateInventory() throws Exception {
        String id = UUID.randomUUID().toString();
        InventoryRequestModel request = InventoryRequestModel.builder().build();

        InventoryResponseModel responseModel = InventoryResponseModel.builder()
                .bookid(id)
                .title("Updated Book")
                .build();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess(
                        new ObjectMapper().writeValueAsString(responseModel),
                        MediaType.APPLICATION_JSON
                ));

        InventoryResponseModel response = client.updateInventory(id, request);
        assertEquals(id, response.getBookid());
    }

    @Test
    void testDeleteInventory() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withSuccess());

        client.deleteInventory(id);
    }

    @Test
    void testGetInventoryById_NotFound() {
        String id = UUID.randomUUID().toString();

        server.expect(requestTo(BASE_URI + "/" + id))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(NotFoundException.class, () -> client.getInventoryByInventoryId(id));
    }

    @Test
    void testAddInventory_InvalidInput() {
        InventoryRequestModel request = InventoryRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));

        assertThrows(InvalidInputException.class, () -> client.addInventory(request));
    }

    @Test
    void testAddInventory_Duplicate() {
        InventoryRequestModel request = InventoryRequestModel.builder().build();

        server.expect(requestTo(BASE_URI))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(DuplicateResourceException.class, () -> client.addInventory(request));
    }
}
