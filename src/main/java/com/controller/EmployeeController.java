package com.controller;

import com.model.Employee;
import com.service.EmployeeService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('READER', 'EDITOR', 'ADMIN')")
    public List<Employee> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('READER', 'EDITOR', 'ADMIN')")
    public Employee getById(@PathVariable String id) {
        return employeeService.getById(new ObjectId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public Employee create(@RequestBody Employee employee) {
        return employeeService.create(employee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        return employeeService.update(new ObjectId(id), employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public void delete(@PathVariable String id) {
        employeeService.delete(new ObjectId(id));
    }
}