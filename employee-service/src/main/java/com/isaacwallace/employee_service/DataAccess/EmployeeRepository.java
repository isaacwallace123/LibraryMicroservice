package com.isaacwallace.employee_service.DataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findEmployeeByEmployeeIdentifier_Employeeid(String employeeIdentifierEmployeeid);

    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    boolean existsByEmailIgnoreCase(String email);
}
