package com.isaacwallace.employee_service.DataAccess;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private EmployeeIdentifier employeeIdentifier;

    private String first_name;
    private String last_name;

    private LocalDate dob;

    private String email;

    @Enumerated(EnumType.STRING)
    private EmployeeTitle title;
    private Double salary;

    public Employee(String first_name, String last_name, LocalDate dob, String email, EmployeeTitle title, Double salary) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.dob = dob;
        this.email = email;
        this.title = title;
        this.salary = salary;
    }
}
