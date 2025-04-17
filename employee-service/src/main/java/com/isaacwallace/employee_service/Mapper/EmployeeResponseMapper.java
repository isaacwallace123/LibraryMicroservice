package com.isaacwallace.employee_service.Mapper;

import com.isaacwallace.employee_service.DataAccess.Employee;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeResponseMapper {
    @Mapping(expression = "java(employee.getEmployeeIdentifier().getEmployeeid())", target = "employeeid")
    @Mapping(target = "age", ignore = true)
    EmployeeResponseModel entityToResponseModel(Employee employee);
    List<EmployeeResponseModel> entityListToResponseModelList(List<Employee> employees);

    @AfterMapping
    default void mapResponseFields(@MappingTarget EmployeeResponseModel postResponseModel, Employee employee) {
        postResponseModel.setAge(Period.between(employee.getDob(), LocalDate.now()).getYears());
    }
}
