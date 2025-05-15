package com.isaacwallace.inventory_service.DomainClient.Transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.inventory_service.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.inventory_service.Utils.Exceptions.HttpErrorInfo;
import com.isaacwallace.inventory_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.inventory_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class TransactionServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;

    public TransactionServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.transaction-service.host}") String SERVICE_HOST, @Value("${app.transaction-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/transactions";
    }

    public void deleteTransactionByInventoryId(String inventoryid) {
        try {
            this.restTemplate.delete(SERVICE_BASE_URL + "/inventory/" + inventoryid);
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
