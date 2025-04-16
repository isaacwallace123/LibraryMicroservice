package com.isaacwallace.employee_service.Mapper;

import com.isaacwallace.employee_service.DataAccess.Employee;
import com.isaacwallace.employee_service.Presentation.EmployeeController;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @AfterMapping
    default void addLinks(@MappingTarget EmployeeResponseModel postResponseModel, Employee employee) {
        Link selfLink = linkTo(methodOn(EmployeeController.class).getEmployee(employee.getEmployeeIdentifier().getEmployeeid())).withSelfRel();
        postResponseModel.add(selfLink);

        Link allLink = linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("employees");
        postResponseModel.add(allLink);
    }
}
