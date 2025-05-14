package com.isaacwallace.employee_service.DataAccess;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    private String firstName;
    private String lastName;

    private LocalDate dob;

    private String email;

    @Enumerated(EnumType.STRING)
    private Title title;

    private Double salary;

    public Employee(@NotNull String firstName, @NotNull String lastName, @NotNull LocalDate dob, @NotNull String email, @NotNull Title title, @NotNull Double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.title = title;
        this.salary = salary;
    }
}
