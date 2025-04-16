package com.isaacwallace.employee_service.Mapper;

import com.isaacwallace.employee_service.DataAccess.Employee;
import com.isaacwallace.employee_service.DataAccess.EmployeeIdentifier;
import com.isaacwallace.employee_service.Presentation.Models.EmployeeRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeRequestMapper {
    @Mapping(target = "id", ignore = true)
    Employee requestModelToEntity(EmployeeRequestModel employeeRequestModel, EmployeeIdentifier employeeIdentifier);
    void updateEntityFromRequest(EmployeeRequestModel employeeRequestModel, @MappingTarget Employee employee);
}
