package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipResponseModel;
import com.isaacwallace.api_gateway.Utils.Exceptions.HttpErrorInfo;
import com.isaacwallace.api_gateway.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.api_gateway.Utils.Exceptions.NotFoundException;
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
public class MembershipServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;

    public MembershipServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.membership-service.host}") String SERVICE_HOST, @Value("${app.membership-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/members";
    }

    public List<MembershipResponseModel> getMembers() {
        try {
            log.debug("membership-service URL is {}", SERVICE_BASE_URL);

            return this.restTemplate.getForObject(SERVICE_BASE_URL, List.class);
        } catch (HttpClientErrorException ex) {
            log.debug(ex.toString());
            throw handleHttpClientException(ex);
        }
    }

    public MembershipResponseModel getMemberByMemberId(String memberid) {
        try {
            log.debug("membership-service URL is {}", SERVICE_BASE_URL + "/" + memberid);

            return this.restTemplate.getForObject(SERVICE_BASE_URL + "/" + memberid, MembershipResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public MembershipResponseModel addMember(MembershipRequestModel membershipRequestModel) {
        try {
            log.debug("membership-service URL is {}", SERVICE_BASE_URL);

            return this.restTemplate.postForObject(SERVICE_BASE_URL, membershipRequestModel, MembershipResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public MembershipResponseModel updateMember(String memberid, MembershipRequestModel membershipRequestModel) {
        try {
            log.debug("membership-service URL is {}", SERVICE_BASE_URL + "/" + memberid);

            this.restTemplate.put(SERVICE_BASE_URL + "/" + memberid, membershipRequestModel);

            return this.getMemberByMemberId(memberid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteMember(String memberid) {
        try {
            log.debug("membership-service URL is {}", SERVICE_BASE_URL + "/" + memberid);

            this.restTemplate.delete(SERVICE_BASE_URL + "/" + memberid);
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
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }

        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }

        log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());

        return ex;
    }
}
