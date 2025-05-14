package com.isaacwallace.transaction_service.DomainClient.Membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.MemberResponseModel;
import com.isaacwallace.transaction_service.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.transaction_service.Utils.Exceptions.HttpErrorInfo;
import com.isaacwallace.transaction_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.transaction_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
public class MembershipServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;

    public MembershipServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.membership-service.host}") String SERVICE_HOST, @Value("${app.membership-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/members";
    }

    public MemberResponseModel getMemberById(String memberId) {
        try {
            return restTemplate.getForObject(SERVICE_BASE_URL + "/" + memberId, MemberResponseModel.class);
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