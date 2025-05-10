package com.isaacwallace.api_gateway.Employee.Presentation.Models;

import com.isaacwallace.api_gateway.Employee.DataAccess.EmployeeTitle;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeRequestModel extends RepresentationModel<EmployeeRequestModel> {
    String firstName;
    String lastName;

    LocalDate dob;

    String email;
    EmployeeTitle title;
    Double salary;
}
