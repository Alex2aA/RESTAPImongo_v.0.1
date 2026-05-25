package com.service;

import com.model.Department;
import com.repository.DepartmentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    public Department getById(ObjectId id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    public void delete(ObjectId id) {
        departmentRepository.deleteById(id);
    }
}