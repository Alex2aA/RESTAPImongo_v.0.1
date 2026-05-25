package com.service;

import com.model.Employee;
import com.repository.EmployeeRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public Employee getById(ObjectId id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee update(ObjectId id, Employee employee) {
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    public void delete(ObjectId id) {
        employeeRepository.deleteById(id);
    }
}