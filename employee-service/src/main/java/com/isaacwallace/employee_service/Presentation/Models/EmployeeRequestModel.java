package com.isaacwallace.employee_service.Presentation.Models;

import com.isaacwallace.employee_service.DataAccess.EmployeeTitle;
import lombok.*;

import java.time.LocalDate;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeRequestModel {
    String first_name;
    String last_name;

    LocalDate dob;

    String email;
    EmployeeTitle title;
    Double salary;
}