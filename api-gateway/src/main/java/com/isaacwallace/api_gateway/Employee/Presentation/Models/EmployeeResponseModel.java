package com.isaacwallace.api_gateway.Employee.Presentation.Models;

import com.isaacwallace.api_gateway.Employee.DataAccess.EmployeeTitle;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
public class EmployeeResponseModel extends RepresentationModel<EmployeeResponseModel> {
    private String employeeid;

    private String firstName;
    private String lastName;

    private LocalDate dob;
    private int age;

    private String email;
    private EmployeeTitle title;
    private Double salary;
}
