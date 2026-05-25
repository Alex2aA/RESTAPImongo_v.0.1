package com.controller;

import com.model.Employee;
import com.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/avg-salary-by-dept")
    public List<Map> avgSalaryByDepartment() {
        return reportService.avgSalaryByDepartment();
    }

    @GetMapping("/employees-above-salary")
    public List<Employee> employeesAboveSalary(@RequestParam double minSalary) {
        return reportService.employeesWithSalaryGreaterThan(minSalary);
    }

    @GetMapping("/count-by-position")
    public List<Map> countByPosition() {
        return reportService.countByPosition();
    }
}