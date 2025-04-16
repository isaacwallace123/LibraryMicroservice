package com.isaacwallace.employee_service.Business;

import com.isaacwallace.employee_service.DataAccess.Employee;
import com.isaacwallace.employee_service.DataAccess.EmployeeIdentifier;
import com.isaacwallace.employee_service.DataAccess.EmployeeRepository;
import com.isaacwallace.employee_service.Mapper.EmployeeRequestMapper;
import com.isaacwallace.employee_service.Mapper.EmployeeResponseMapper;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeRequestModel;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeResponseModel;
import com.isaacwallace.employee_service.Utils.Exceptions.InUseException;
import com.isaacwallace.employee_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.employee_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<EmployeeResponseModel> getAllEmployees() {
        return this.employeeResponseMapper.entityListToResponseModelList(this.employeeRepository.findAll());
    }

    public EmployeeResponseModel getEmployeeById(String employeeid) {
        Employee employee = this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid(employeeid);

        if (employee == null) {
            throw new NotFoundException("Unknown userId: " + employeeid);
        }

        return this.employeeResponseMapper.entityToResponseModel(employee);
    }

    public EmployeeResponseModel addEmployee(EmployeeRequestModel employeeRequestModel) {
        Employee newEmployee = this.employeeRequestMapper.requestModelToEntity(employeeRequestModel, new EmployeeIdentifier());

        return employeeResponseMapper.entityToResponseModel(this.employeeRepository.save(newEmployee));
    }

    public EmployeeResponseModel updateEmployee(String employeeid, EmployeeRequestModel employeeRequestModel) {
        Employee employee = this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid(employeeid);

        if (employee == null) {
            throw new NotFoundException("Unknown employeeid: " + employeeid);
        }

        this.employeeRequestMapper.updateEntityFromRequest(employeeRequestModel, employee);

        Employee updatedEmployee = this.employeeRepository.save(employee);

        log.info("Updated employee with employeeid: " + employeeid);

        return this.employeeResponseMapper.entityToResponseModel(updatedEmployee);
    }

    public void deleteEmployee(String employeeid) {
        Employee employee = this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid(employeeid);

        if (employee == null) {
            throw new NotFoundException("Unknown employeeid: " + employeeid);
        }

        try {
            this.employeeRepository.delete(employee);
        } catch (DataIntegrityViolationException exception) {
            throw new InUseException("Employee is in use by another entity, currently cannot delete.");
        }
    }
}
