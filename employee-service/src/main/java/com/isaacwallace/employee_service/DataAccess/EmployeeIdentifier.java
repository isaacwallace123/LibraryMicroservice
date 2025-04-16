package com.isaacwallace.employee_service.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class EmployeeIdentifier {
    @Column(name = "employeeid")
    private String employeeid;

    public EmployeeIdentifier() {
        this.employeeid = UUID.randomUUID().toString();
    }

    public EmployeeIdentifier(String employeeid) {
        this.employeeid = employeeid;
    }
}
