package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorResponseModel;
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
public class AuthorServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;

    public AuthorServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.author-service.host}") String SERVICE_HOST, @Value("${app.author-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/authors";
    }

    public List<AuthorResponseModel> getAuthors() {
        try {
            log.debug("author-service URL is {}", SERVICE_BASE_URL);

            ResponseEntity<List<AuthorResponseModel>> response = restTemplate.exchange(SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<AuthorResponseModel>>() {});

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.debug(ex.toString());
            throw handleHttpClientException(ex);
        }
    }

    public AuthorResponseModel getAuthorByAuthorId(String authorid) {
        try {
            log.debug("author-service URL is {}", SERVICE_BASE_URL + "/" + authorid);

            return this.restTemplate.getForObject(SERVICE_BASE_URL + "/" + authorid, AuthorResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AuthorResponseModel addAuthor(AuthorRequestModel authorRequestModel) {
        try {
            log.debug("author-service URL is {}", SERVICE_BASE_URL);

            return this.restTemplate.postForObject(SERVICE_BASE_URL, authorRequestModel, AuthorResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AuthorResponseModel updateAuthor(String authorid, AuthorRequestModel authorRequestModel) {
        try {
            log.debug("author-service URL is {}", SERVICE_BASE_URL + "/" + authorid);

            this.restTemplate.put(SERVICE_BASE_URL + "/" + authorid, authorRequestModel);

            return this.getAuthorByAuthorId(authorid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteAuthor(String authorid) {
        try {
            log.debug("author-service URL is {}", SERVICE_BASE_URL + "/" + authorid);

            this.restTemplate.delete(SERVICE_BASE_URL + "/" + authorid);
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