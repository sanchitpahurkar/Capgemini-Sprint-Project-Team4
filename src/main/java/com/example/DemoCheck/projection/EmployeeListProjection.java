package com.example.DemoCheck.projection;

import com.example.DemoCheck.entity.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "employeeList",types = Employee.class)
public interface EmployeeListProjection {
    Integer getEmployeeNumber();
    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
    String getExtension();
    String getEmail();
    String getJobTitle();
}
