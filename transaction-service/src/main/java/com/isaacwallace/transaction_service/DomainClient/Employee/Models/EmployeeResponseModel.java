package com.isaacwallace.transaction_service.DomainClient.Employee.Models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponseModel {
    private String employeeid;

    private String firstName;
    private String lastName;

    private LocalDate dob;
    private int age;

    private String email;
    private Title title;
    private Double salary;
}