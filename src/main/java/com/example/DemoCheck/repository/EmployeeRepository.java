package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "employees")
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

    // 1. Custom Query for Full Name Search (Handles spaces between first and last name)
    @RestResource(path = "byName", rel = "byName")
    @Query("SELECT e FROM Employee e WHERE LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Employee> searchName(@Param("name") String name, Pageable pageable);

    // 2. Standard Spring Data Method for Office Code
    @RestResource(path = "byOfficeCode", rel = "byOfficeCode")
    Page<Employee> findByOfficeOfficeCode(@Param("officeCode") String officeCode, Pageable pageable);

    @RestResource(path="byReportsTo" , rel="byReportsTo")
    Page<Employee> findByManagerEmployeeNumber(@Param("reportsTo") Integer reportsTo , Pageable pageable);

    // 3. Standard Spring Data Method for Job Title
    @RestResource(path = "byJobTitle", rel = "byJobTitle")
    Page<Employee> findByJobTitleContainingIgnoreCase(@Param("jobTitle") String jobTitle, Pageable pageable);

}
