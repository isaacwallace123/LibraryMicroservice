package com.isaacwallace.api_gateway.Services.Employee.Presentation.Models;

import com.isaacwallace.api_gateway.Services.Employee.DataAccess.Title;
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
    Title title;
    Double salary;
}
