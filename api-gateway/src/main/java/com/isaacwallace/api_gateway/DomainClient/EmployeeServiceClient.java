package com.isaacwallace.api_gateway.DomainClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
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
public class EmployeeServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private String SERVICE_BASE_URL;

    public EmployeeServiceClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.employee-service.host}") String SERVICE_HOST, @Value("${app.employee-service.port}") String SERVICE_PORT) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.SERVICE_BASE_URL = "http://" + SERVICE_HOST + ":" + SERVICE_PORT + "/api/v1/employees";
    }

    public List<EmployeeResponseModel> getEmployees() {
        try {
            log.debug("employee-service URL is {}", SERVICE_BASE_URL);

            ResponseEntity<List<EmployeeResponseModel>> response = restTemplate.exchange(SERVICE_BASE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<EmployeeResponseModel>>() {} );

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.debug(ex.toString());
            throw handleHttpClientException(ex);
        }
    }

    public EmployeeResponseModel getEmployeeByEmployeeId(String employeeid) {
        try {
            log.debug("employee-service URL is {}", SERVICE_BASE_URL + "/" + employeeid);

            return this.restTemplate.getForObject(SERVICE_BASE_URL + "/" + employeeid, EmployeeResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel) {
        try {
            log.debug("employee-service URL is {}", SERVICE_BASE_URL);

            return this.restTemplate.postForObject(SERVICE_BASE_URL, employeeRequestModel, EmployeeResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel) {
        try {
            log.debug("employee-service URL is {}", SERVICE_BASE_URL + "/" + employeeid);

            this.restTemplate.put(SERVICE_BASE_URL + "/" + employeeid, employeeRequestModel);

            return this.getEmployeeByEmployeeId(employeeid);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void deleteEmployee(String employeeid) {
        try {
            log.debug("employee-service URL is {}", SERVICE_BASE_URL + "/" + employeeid);

            this.restTemplate.delete(SERVICE_BASE_URL + "/" + employeeid);
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
