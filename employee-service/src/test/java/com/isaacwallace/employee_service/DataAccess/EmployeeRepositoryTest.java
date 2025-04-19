package com.isaacwallace.employee_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    void testEmployeeConstructorAndGetters() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), employee.getDob());
        assertEquals("JohnDoe@me.com", employee.getEmail());
        assertEquals(EmployeeTitle.ADMINISTRATOR, employee.getTitle());
        assertEquals(1000.00, employee.getSalary());
    }

    @Test
    void testSaveEmployee() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employeeRepository.save(employee);

        assertEquals(1, employeeRepository.count());
    }

    @Test
    void toStringContainsAllFields() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setId(1);
        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        String str = employee.toString();

        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals(LocalDate.of(2000, 1, 1), employee.getDob());
        assertEquals("JohnDoe@me.com", employee.getEmail());
        assertEquals(EmployeeTitle.ADMINISTRATOR, employee.getTitle());
        assertEquals(1000.00, employee.getSalary());
    }

    @Test
    void testEqualsSameObject() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setId(1);
        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        assertEquals(employee, employee);
    }

    @Test
    void testEqualsDifferentClass() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        assertNotEquals(employee, "NotEmployee");
    }

    @Test
    void testEquals_Null() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        assertNotEquals(employee, null);
    }

    @Test
    void testNotEqualsDifferentFields() {
        Employee e1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        e1.setEmployeeIdentifier(new EmployeeIdentifier("abc-123"));
        e1.setId(1);

        Employee e2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        e2.setEmployeeIdentifier(new EmployeeIdentifier("xyz-456"));
        e1.setId(2);

        assertNotEquals(e1, e2);
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testEqualsNullAndDifferentClass() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        employee.setEmployeeIdentifier(new EmployeeIdentifier("abc-123"));
        employee.setId(1);

        assertNotEquals(null, employee);           // null check
        assertNotEquals("some string", employee);  // different class
    }

    @Test
    void testEquals_DifferentId() {
        Employee e1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee e2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        e1.setEmployeeIdentifier(new EmployeeIdentifier("123"));
        e2.setEmployeeIdentifier(new EmployeeIdentifier("456"));

        assertNotEquals(e1, e2);
    }

    @Test
    void testHashCode_DifferentObjects() {
        Employee e1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee e2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setEmployeeIdentifier(new EmployeeIdentifier("abc-123"));
        employee.setId(1);

        int hash1 = employee.hashCode();

        assertEquals(hash1, employee.hashCode()); // same hash on repeated calls

        employee.setEmail("NewRandomEmail");

        int hash2 = employee.hashCode();

        assertNotEquals(hash2, hash1);
    }

    @Test
    public void whenEmployeesExists_thenReturnAllEmployees() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        this.employeeRepository.save(employee1);
        this.employeeRepository.save(employee2);

        long afterSizeDB = this.employeeRepository.count();

        List<Employee> Employees = this.employeeRepository.findAll();

        assertNotNull(Employees);
        assertNotEquals(0, Employees.size());
        assertEquals(afterSizeDB, Employees.size());
    }

    @Test
    void testEquals_DifferentFirstName() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_DifferentLastName() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Test", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_DifferentDob() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(1999, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_DifferentEmail() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JaneDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_DifferentTitle() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.MANAGER, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_DifferentSalary() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 2000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_DifferentIdentifier() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    void testEquals_MixedNullFields() {
        Employee employee1 = new Employee("John", null, LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("Jane", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        assertNotEquals(employee1, employee2);
    }

    @Test
    public void whenEmployeeExists_thenReturnEmployeeByEmployeeId() {
        Employee employee = new Employee("John", null, LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        this.employeeRepository.save(employee);

        Employee foundEmployee = this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid(employee.getEmployeeIdentifier().getEmployeeid());

        assertNotNull(foundEmployee);

        assertEquals(employee.getEmployeeIdentifier().getEmployeeid(), foundEmployee.getEmployeeIdentifier().getEmployeeid());
        assertEquals(employee.getFirstName(), foundEmployee.getFirstName());
        assertEquals(employee.getLastName(), foundEmployee.getLastName());
        assertEquals(employee.getDob(), foundEmployee.getDob());
        assertEquals(employee.getEmail(), foundEmployee.getEmail());
        assertEquals(employee.getTitle(), foundEmployee.getTitle());
        assertEquals(employee.getSalary(), foundEmployee.getSalary());
    }

    @Test
    void whenEmployeeDoesNotExist_thenReturnNull() {
        Employee employee = this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid("00000000-0000-0000-0000-000000000000");

        assertNull(employee);
    }

    @Test
    void whenValidEntitySaved_thenPersistentAndReturn() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        Employee savedEmployee = this.employeeRepository.save(employee);

        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeIdentifier().getEmployeeid());

        assertEquals(employee.getFirstName(), savedEmployee.getFirstName());
        assertEquals(employee.getLastName(), savedEmployee.getLastName());
        assertEquals(employee.getDob(), savedEmployee.getDob());
        assertEquals(employee.getEmail(), savedEmployee.getEmail());
        assertEquals(employee.getTitle(), savedEmployee.getTitle());
        assertEquals(employee.getSalary(), savedEmployee.getSalary());
    }

    @Test
    void whenValidEntityDeleted_thenPersistentAndReturn() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        this.employeeRepository.save(employee);
        this.employeeRepository.delete(employee);

        assertNull(this.employeeRepository.findEmployeeByEmployeeIdentifier_Employeeid(employee.getEmployeeIdentifier().getEmployeeid()));
    }

    @Test
    void testExistsByFirstNameAndLastNameIgnoreCase() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        this.employeeRepository.save(employee);

        assertTrue(this.employeeRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(employee.getFirstName(), employee.getLastName()));
        assertFalse(this.employeeRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("johnny", "doe"));
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        Employee employee = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        employee.setEmployeeIdentifier(new EmployeeIdentifier());

        this.employeeRepository.save(employee);

        assertTrue(this.employeeRepository.existsByEmailIgnoreCase(employee.getEmail()));
        assertFalse(this.employeeRepository.existsByEmailIgnoreCase("johnnyTest@me.com"));
    }

    @Test
    void testSavingDuplicateEmployees() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        employee1.setEmployeeIdentifier(new EmployeeIdentifier());
        employee2.setEmployeeIdentifier(new EmployeeIdentifier());

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        List<Employee> employees = employeeRepository.findAll();
        assertEquals(2, employees.size());
    }

    @Test
    void testDeleteAllEmployees() {
        Employee employee1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee employee2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        employee1.setEmployeeIdentifier(new EmployeeIdentifier());
        employee2.setEmployeeIdentifier(new EmployeeIdentifier());

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        employeeRepository.deleteAll();
        List<Employee> employees = employeeRepository.findAll();
        assertEquals(0, employees.size());
    }

    @Test
    void testRepositoryIsEmptyInitially() {
        List<Employee> employees = employeeRepository.findAll();
        assertTrue(employees.isEmpty());
    }

    @Test
    void testDefaultConstructorSetsIdCorrectly() {
        String testId = "testId";
        EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier(testId);

        assertNotNull(employeeIdentifier);
        assertEquals(testId, employeeIdentifier.getEmployeeid());
    }

    @Test
    void testTwoDefaultConstructorsGenerateDifferentIds() {
        EmployeeIdentifier employeeIdentifier1 = new EmployeeIdentifier();
        EmployeeIdentifier employeeIdentifier2 = new EmployeeIdentifier();

        assertNotEquals(employeeIdentifier1.getEmployeeid(), employeeIdentifier2.getEmployeeid());
    }

    @Test
    void testEmployeeIdIsNotNullOrEmpty() {
        EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
        assertNotNull(employeeIdentifier.getEmployeeid());
        assertNotEquals("", employeeIdentifier.getEmployeeid());
    }

    @Test
    void testCustomIdCanBeUuidString() {
        String uuid = UUID.randomUUID().toString();
        EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier(uuid);

        assertDoesNotThrow(() -> UUID.fromString(employeeIdentifier.getEmployeeid()));
        assertEquals(uuid, employeeIdentifier.getEmployeeid());
    }

    @Test
    void testEqualsIdenticalValuesDifferentInstances() {
        EmployeeIdentifier id = new EmployeeIdentifier("same-id");

        Employee e1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee e2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        e1.setId(1);
        e2.setId(1);
        e1.setEmployeeIdentifier(id);
        e2.setEmployeeIdentifier(id);

        assertEquals(e1, e2);
    }

    @Test
    void testEqualsDifferentEmployeeIdentifier() {
        Employee e1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee e2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "JohnDoe@me.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        e1.setId(1);
        e2.setId(1);
        e1.setEmployeeIdentifier(new EmployeeIdentifier("id1"));
        e2.setEmployeeIdentifier(new EmployeeIdentifier("id2"));

        assertNotEquals(e1, e2);
    }

    @Test
    void testHashCodeConsistencyForSameState() {
        EmployeeIdentifier identifier = new EmployeeIdentifier("abc");

        Employee e1 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "email@example.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee e2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "email@example.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        e1.setId(1);
        e2.setId(1);
        e1.setEmployeeIdentifier(identifier);
        e2.setEmployeeIdentifier(identifier);

        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testEqualsWithNullFields() {
        Employee e1 = new Employee(null, null, null, null, null, null);
        Employee e2 = new Employee(null, null, null, null, null, null);

        e1.setId(null);
        e2.setId(null);

        assertEquals(e1, e2);
    }

    @Test
    void testEqualsWithOneNullField() {
        Employee e1 = new Employee("John", null, LocalDate.of(2000, 1, 1), "email@example.com", EmployeeTitle.ADMINISTRATOR, 1000.00);
        Employee e2 = new Employee("John", "Doe", LocalDate.of(2000, 1, 1), "email@example.com", EmployeeTitle.ADMINISTRATOR, 1000.00);

        assertNotEquals(e1, e2);
    }

    @Test
    void testCanEqual() {
        Employee employee = new Employee();
        assertTrue(employee.canEqual(new Employee()));
        assertFalse(employee.canEqual("Not an employee"));
    }

}