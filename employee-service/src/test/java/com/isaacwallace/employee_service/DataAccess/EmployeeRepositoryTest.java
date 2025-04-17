package com.isaacwallace.employee_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();
    }


}