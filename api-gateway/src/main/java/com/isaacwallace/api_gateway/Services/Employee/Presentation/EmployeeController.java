package com.isaacwallace.api_gateway.Services.Employee.Presentation;

import com.isaacwallace.api_gateway.Services.Employee.Business.EmployeeService;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EmployeeResponseModel>> getEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(this.employeeService.getAllEmployees());
    }

    @GetMapping(value = "{employeeid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseModel> getEmployeeById(@PathVariable String employeeid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.employeeService.getEmployeeById(employeeid));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseModel> addEmployee(@RequestBody EmployeeRequestModel employeeRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.employeeService.addEmployee(employeeRequestModel));
    }

    @PutMapping(value = "{employeeid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseModel> updateEmployee(@PathVariable String employeeid, @RequestBody EmployeeRequestModel employeeRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.employeeService.updateEmployee(employeeid, employeeRequestModel));
    }

    @DeleteMapping(value = "{employeeid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseModel> deleteEmployee(@PathVariable String employeeid) {
        this.employeeService.deleteEmployee(employeeid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
