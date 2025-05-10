package com.isaacwallace.inventory_service.DomainClient;

import com.isaacwallace.inventory_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AuthorServiceClient {
    private final RestTemplate restTemplate;

    private String SERVICE_BASE_URL;

    public AuthorServiceClient(RestTemplate restTemplate, @Value("${app.author-service.host}") String SERVICE_HOST, @Value("${app.author-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/authors";
    }

    public void validateAuthorExists(String authorId) {
        try {
            restTemplate.getForObject(SERVICE_BASE_URL + "/" + authorId, Object.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Unknown authorid: " + authorId);
        }
    }
}
