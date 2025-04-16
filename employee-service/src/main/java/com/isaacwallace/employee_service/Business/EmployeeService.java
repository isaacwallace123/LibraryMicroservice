package com.isaacwallace.employee_service.Business;

import com.isaacwallace.employee_service.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeResponseModel;

import java.util.List;

public interface EmployeeService {
    public List<EmployeeResponseModel> getAllEmployees();
    public EmployeeResponseModel getEmployeeById(String employeeid);
    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel);
    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel);
    public void deleteEmployee(String employeeid);
}
