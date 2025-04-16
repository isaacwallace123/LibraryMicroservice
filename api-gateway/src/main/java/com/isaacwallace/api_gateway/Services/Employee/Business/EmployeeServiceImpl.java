package com.isaacwallace.api_gateway.Services.Employee.Business;

import com.isaacwallace.api_gateway.DomainClient.EmployeeServiceClient;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeServiceClient employeeServiceClient;

    public EmployeeServiceImpl(EmployeeServiceClient employeeServiceClient) {
        this.employeeServiceClient = employeeServiceClient;
    }

    public List<EmployeeResponseModel> getAllEmployees() {
        return this.employeeServiceClient.getEmployees();
    }

    public EmployeeResponseModel getEmployeeById(String employeeid) {
        return this.employeeServiceClient.getEmployeeByEmployeeId(employeeid);
    }

    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel) {
        return this.employeeServiceClient.addEmployee(employeeRequestModel);
    }

    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel) {
        return this.employeeServiceClient.updateEmployee(employeeid, employeeRequestModel);
    }

    public void deleteEmployee(String employeeid) {
        this.employeeServiceClient.deleteEmployee(employeeid);
    }
}
