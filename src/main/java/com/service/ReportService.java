package com.service;

import com.model.Employee;
import com.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    // 1. Средняя зарплата по отделам (с $lookup)
    public List<Map> avgSalaryByDepartment() {
        LookupOperation lookup = LookupOperation.newLookup()
                .from("departments")
                .localField("department_id")
                .foreignField("_id")
                .as("dept");
        Aggregation aggregation = Aggregation.newAggregation(
                lookup,
                Aggregation.unwind("dept"),
                Aggregation.group("dept.name").avg("salary").as("avg_salary"),
                Aggregation.project("avg_salary").and("_id").as("department")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "employees", Map.class);
        return results.getMappedResults();
    }

    // 2. Простой запрос: сотрудники с зарплатой выше minSalary
    public List<Employee> employeesWithSalaryGreaterThan(double minSalary) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getSalary() != null && e.getSalary() > minSalary)
                .collect(Collectors.toList());
    }

    // 3. Группировка по должностям
    public List<Map> countByPosition() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("position").count().as("count"),
                Aggregation.project("count").and("_id").as("position")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "employees", Map.class);
        return results.getMappedResults();
    }
}