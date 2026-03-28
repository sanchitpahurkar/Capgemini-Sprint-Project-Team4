package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Employee;
import com.example.DemoCheck.entity.Office;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryTest {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OfficeRepository officeRepository; // ✅ REQUIRED

    private Office createOffice() {
        Office office = new Office();
        office.setOfficeCode("1");
        office.setCity("Nagpur");
        office.setPhone("1234567890");
        office.setAddressLine1("IT Park");
        office.setCountry("India");
        office.setPostalCode("440022");
        office.setTerritory("APAC");
        return officeRepository.save(office);
    }

    // ✅ Helper: Create Employee
    private Employee createEmployee(int id, Office office) {
        Employee emp = new Employee();
        emp.setEmployeeNumber(id);
        emp.setFirstName("Test" + id);
        emp.setLastName("User");
        emp.setEmail("test" + id + "@gmail.com");
        emp.setExtension("x1234");
        emp.setJobTitle("Developer");
        emp.setOffice(office); // ✅ CORRECT

        return emp;
    }

    // ✅ Test: Get All Employees
    @Test
    @DisplayName("Test: Get All Employees")
    public void getAllEmployees() {

        Office office = createOffice();

        int employeesOld = employeeRepository.findAll().size();

        employeeRepository.save(createEmployee(9999, office));

        int employeesNew = employeeRepository.findAll().size();

        assertEquals(employeesOld + 1, employeesNew);
    }

    // ✅ Pagination Test - First Page
    @Test
    @DisplayName("Test: Pagination - first page")
    public void testPagination() {

        Office office = createOffice();

        for (int i = 0; i < 50; i++) {
            employeeRepository.save(createEmployee(1000 + i, office));
        }

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employee> page = employeeRepository.findAll(pageable);

        assertEquals(5, page.getContent().size());
        assertEquals(0, page.getNumber());
        assertTrue(page.getTotalPages() >= 10);
    }

    // ✅ Pagination Test - Second Page
    @Test
    @DisplayName("Test: Pagination - second page")
    public void testSecondPage() {

        Office office = createOffice();

        for (int i = 0; i < 20; i++) {
            employeeRepository.save(createEmployee(2000 + i, office));
        }

        Pageable pageable = PageRequest.of(1, 5);

        Page<Employee> page = employeeRepository.findAll(pageable);

        assertEquals(5, page.getContent().size());
        assertEquals(1, page.getNumber());
    }
}