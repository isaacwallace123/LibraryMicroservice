package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Inventory.Presentation.Models.InventoryRequestModel;
import com.isaacwallace.api_gateway.Inventory.Presentation.Models.InventoryResponseModel;
import com.isaacwallace.api_gateway.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.api_gateway.Utils.Exceptions.HttpErrorInfo;
import com.isaacwallace.api_gateway.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.api_gateway.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class InventoryServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;

    public InventoryServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.inventory-service.host}") String SERVICE_HOST, @Value("${app.inventory-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/inventory";
    }

    public List<InventoryResponseModel> getInventorys() {
        try {
            log.debug("inventory-service URL is {}", SERVICE_BASE_URL);

            ResponseEntity<List<InventoryResponseModel>> response = restTemplate.exchange(SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<InventoryResponseModel>>() {});

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.debug(ex.toString());
            throw handleHttpClientException(ex);
        }
    }


    public InventoryResponseModel getInventoryByInventoryId(String inventoryid) {
        try {
            log.debug("inventory-service URL is {}", SERVICE_BASE_URL + "/" + inventoryid);

            return this.restTemplate.getForObject(SERVICE_BASE_URL + "/" + inventoryid, InventoryResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    public InventoryResponseModel addInventory(InventoryRequestModel inventoryRequestModel) {
        try {
            log.debug("inventory-service URL is {}", SERVICE_BASE_URL);

            return this.restTemplate.postForObject(SERVICE_BASE_URL, inventoryRequestModel, InventoryResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    public InventoryResponseModel updateInventory(String inventoryid, InventoryRequestModel inventoryRequestModel) {
        try {
            log.debug("inventory-service URL is {}", SERVICE_BASE_URL + "/" + inventoryid);

            this.restTemplate.put(SERVICE_BASE_URL + "/" + inventoryid, inventoryRequestModel);

            return this.getInventoryByInventoryId(inventoryid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    public void deleteInventory(String inventoryid) {
        try {
            log.debug("inventory-service URL is {}", SERVICE_BASE_URL + "/" + inventoryid);

            this.restTemplate.delete(SERVICE_BASE_URL + "/" + inventoryid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public String getErrorMessage(HttpClientErrorException ex) {
        try {
            return this.mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
            return new NotFoundException(getErrorMessage(ex));

        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
            return new InvalidInputException(getErrorMessage(ex));

        if (ex.getStatusCode() == HttpStatus.CONFLICT)
            return new DuplicateResourceException(getErrorMessage(ex));

        log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());

        return ex;
    }
}
