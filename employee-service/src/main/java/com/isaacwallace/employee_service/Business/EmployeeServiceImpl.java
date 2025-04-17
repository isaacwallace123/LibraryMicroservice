package com.isaacwallace.employee_service.Business;

import com.isaacwallace.employee_service.DataAccess.Employee;
import com.isaacwallace.employee_service.DataAccess.EmployeeIdentifier;
import com.isaacwallace.employee_service.DataAccess.EmployeeRepository;
import com.isaacwallace.employee_service.DataAccess.EmployeeTitle;
import com.isaacwallace.employee_service.Mapper.EmployeeRequestMapper;
import com.isaacwallace.employee_service.Mapper.EmployeeResponseMapper;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeResponseModel;
import com.isaacwallace.employee_service.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.employee_service.Utils.Exceptions.InUseException;
import com.isaacwallace.employee_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.employee_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeResponseMapper employeeResponseMapper;
    private final EmployeeRequestMapper employeeRequestMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeResponseMapper employeeResponseMapper, EmployeeRequestMapper employeeRequestMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeResponseMapper = employeeResponseMapper;
        this.employeeRequestMapper = employeeRequestMapper;
    }

    private void validateEmployeeRequestModel(EmployeeRequestModel model) {
        if (model.getFirstName() == null || model.getFirstName().isBlank()) {
            throw new InvalidInputException("Invalid firstName: " + model.getFirstName());
        }
        if (model.getLastName() == null || model.getLastName().isBlank()) {
            throw new InvalidInputException("Invalid lastName: " + model.getLastName());
        }
        if (model.getEmail() == null || model.getEmail().isBlank()) {
            throw new InvalidInputException("Invalid email: " + model.getEmail());
        }

        if (model.getDob() == null) {
            throw new InvalidInputException("Invalid dob: Input missing.");
        }
        if (model.getSalary() == null) {
            throw new InvalidInputException("Invalid salary: Input missing.");
        }

        try {
            EmployeeTitle.valueOf(model.getTitle().toString());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid title enum: " + model.getTitle());
        }

        if (this.employeeRepository.existsByEmailIgnoreCase(model.getEmail())) {
            throw new DuplicateResourceException("Duplicate email: " + model.getEmail());
        }

        if (this.employeeRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(model.getFirstName(), model.getLastName())) {
            throw new DuplicateResourceException("Duplicate name: " + model.getFirstName() + " " + model.getLastName());
        }
    }

    private Employee getEmployeeObjectById(String employeeid) {
        try {
            UUID.fromString(employeeid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid employeeid: " + employeeid);
        }

        Employee employee = this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid(employeeid);

        if (employee == null) {
            throw new NotFoundException("Unknown employeeid: " + employeeid);
        }

        return employee;
    }

    public List<EmployeeResponseModel> getAllEmployees() {
        return this.employeeResponseMapper.entityListToResponseModelList(this.employeeRepository.findAll());
    }

    public EmployeeResponseModel getEmployeeById(String employeeid) {
        Employee employee = this.getEmployeeObjectById(employeeid);

        return this.employeeResponseMapper.entityToResponseModel(employee);
    }

    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel) {
        Employee newEmployee = this.employeeRequestMapper.requestModelToEntity(employeeRequestModel, new EmployeeIdentifier());

        this.validateEmployeeRequestModel(employeeRequestModel);

        return employeeResponseMapper.entityToResponseModel(this.employeeRepository.save(newEmployee));
    }

    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel) {
        Employee employee = this.getEmployeeObjectById(employeeid);

        this.validateEmployeeRequestModel(employeeRequestModel);

        this.employeeRequestMapper.updateEntityFromRequest(employeeRequestModel, employee);

        Employee updatedEmployee = this.employeeRepository.save(employee);

        return this.employeeResponseMapper.entityToResponseModel(updatedEmployee);
    }

    public void deleteEmployee(String employeeid) {
        Employee employee = this.getEmployeeObjectById(employeeid);

        try {
            this.employeeRepository.delete(employee);
        } catch (DataIntegrityViolationException exception) {
            throw new InUseException("Employee with id: " + employeeid + " is already in use by another entity.");
        }
    }
}
