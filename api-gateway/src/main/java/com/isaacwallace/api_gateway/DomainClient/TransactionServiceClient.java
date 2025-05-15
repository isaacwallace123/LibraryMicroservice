package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionResponseModel;
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
public class TransactionServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;
    private String AGGREGATE_ROOT_BASE_URL;

    public TransactionServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.transaction-service.host}") String SERVICE_HOST, @Value("${app.transaction-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/transactions";
        this.AGGREGATE_ROOT_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/members/";
    }

    public List<TransactionResponseModel> getTransactions() {
        try {
            log.debug("transaction-service URL is {}", SERVICE_BASE_URL);

            ResponseEntity<List<TransactionResponseModel>> response = restTemplate.exchange(SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<TransactionResponseModel>>() {});

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.debug(ex.toString());
            throw handleHttpClientException(ex);
        }
    }

    public TransactionResponseModel getTransactionByTransactionId(String transactionid) {
        try {
            log.debug("transaction-service URL is {}", SERVICE_BASE_URL + "/" + transactionid);

            return this.restTemplate.getForObject(SERVICE_BASE_URL + "/" + transactionid, TransactionResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public TransactionResponseModel addTransaction(TransactionRequestModel transactionRequestModel) {
        try {
            log.debug("transaction-service URL is {}", SERVICE_BASE_URL);

            return this.restTemplate.postForObject(SERVICE_BASE_URL, transactionRequestModel, TransactionResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public TransactionResponseModel updateTransaction(String transactionid, TransactionRequestModel transactionRequestModel) {
        try {
            log.debug("transaction-service URL is {}", SERVICE_BASE_URL + "/" + transactionid);

            this.restTemplate.put(SERVICE_BASE_URL + "/" + transactionid, transactionRequestModel);

            return this.getTransactionByTransactionId(transactionid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteTransaction(String transactionid) {
        try {
            log.debug("transaction-service URL is {}", SERVICE_BASE_URL + "/" + transactionid);

            this.restTemplate.delete(SERVICE_BASE_URL + "/" + transactionid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<TransactionResponseModel> getTransactionsFromMember(String memberid) {
        try {
            log.debug("transaction-service URL is {}", AGGREGATE_ROOT_BASE_URL + memberid + "/transactions");

            ResponseEntity<List<TransactionResponseModel>> response = restTemplate.exchange(AGGREGATE_ROOT_BASE_URL + memberid + "/transactions", HttpMethod.GET, null, new ParameterizedTypeReference<List<TransactionResponseModel>>() {});

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.debug(ex.toString());
            throw handleHttpClientException(ex);
        }
    }

    public TransactionResponseModel getMemberTransactionById(String memberid, String transactionid) {
        try {
            log.debug("transaction-service URL is {}", AGGREGATE_ROOT_BASE_URL + memberid + "/transactions/" + transactionid);

            return this.restTemplate.getForObject(AGGREGATE_ROOT_BASE_URL + memberid + "/transactions/" + transactionid, TransactionResponseModel.class);
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
