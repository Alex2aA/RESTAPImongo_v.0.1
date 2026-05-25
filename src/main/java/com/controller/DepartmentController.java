package com.controller;

import com.model.Department;
import com.service.DepartmentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('READER', 'EDITOR', 'ADMIN')")
    public List<Department> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('READER', 'EDITOR', 'ADMIN')")
    public Department getById(@PathVariable String id) {
        return departmentService.getById(new ObjectId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Department create(@RequestBody Department department) {
        return departmentService.create(department);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String id) {
        departmentService.delete(new ObjectId(id));
    }
}