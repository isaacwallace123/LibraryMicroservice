package com.isaacwallace.api_gateway.Services.Employee.Presentation.Models;

import com.isaacwallace.api_gateway.Services.Employee.DataAccess.Title;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponseModel extends RepresentationModel<EmployeeResponseModel> {
    private String employeeid;

    private String firstName;
    private String lastName;

    private LocalDate dob;
    private int age;

    private String email;
    private Title title;
    private Double salary;
}
