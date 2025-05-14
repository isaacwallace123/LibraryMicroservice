package com.isaacwallace.employee_service.Presentation.Models;

import com.isaacwallace.employee_service.DataAccess.Title;
import lombok.*;

import java.time.LocalDate;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeRequestModel {
    String firstName;
    String lastName;

    LocalDate dob;

    String email;
    Title title;
    Double salary;
}