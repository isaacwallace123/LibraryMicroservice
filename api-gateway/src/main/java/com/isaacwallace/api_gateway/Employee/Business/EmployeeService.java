package com.isaacwallace.api_gateway.Employee.Business;

import com.isaacwallace.api_gateway.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Employee.Presentation.Models.EmployeeResponseModel;

import java.util.List;

public interface EmployeeService {
    public List<EmployeeResponseModel> getAllEmployees();
    public EmployeeResponseModel getEmployeeById(String employeeid);
    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel);
    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel);
    public void deleteEmployee(String employeeid);
}
