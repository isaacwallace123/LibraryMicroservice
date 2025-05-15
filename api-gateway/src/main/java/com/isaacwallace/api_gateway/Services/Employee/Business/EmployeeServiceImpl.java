package com.isaacwallace.api_gateway.Services.Employee.Business;

import com.isaacwallace.api_gateway.DomainClient.EmployeeServiceClient;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.EmployeeController;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeServiceClient employeeServiceClient;

    public EmployeeServiceImpl(EmployeeServiceClient employeeServiceClient) {
        this.employeeServiceClient = employeeServiceClient;
    }

    public List<EmployeeResponseModel> getAllEmployees() {
        return this.employeeServiceClient.getEmployees().stream().map(this::addLinks).toList();
    }

    public EmployeeResponseModel getEmployeeById(String employeeid) {
        return this.addLinks(this.employeeServiceClient.getEmployeeByEmployeeId(employeeid));
    }

    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel) {
        return this.addLinks(this.employeeServiceClient.addEmployee(employeeRequestModel));
    }

    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel) {
        return this.addLinks(this.employeeServiceClient.updateEmployee(employeeid, employeeRequestModel));
    }

    public void deleteEmployee(String employeeid) {
        this.employeeServiceClient.deleteEmployee(employeeid);
    }

    private EmployeeResponseModel addLinks(EmployeeResponseModel employeeResponseModel) {
        Link selfLink = linkTo(methodOn(EmployeeController.class)
                .getEmployeeById(employeeResponseModel.getEmployeeid()))
                .withSelfRel();
        employeeResponseModel.add(selfLink);

        Link allLink = linkTo(methodOn(EmployeeController.class)
                .getEmployees())
                .withRel("employees");
        employeeResponseModel.add(allLink);

        return employeeResponseModel;
    }
}
