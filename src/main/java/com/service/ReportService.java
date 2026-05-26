package com.service;

import com.config.MongoTemplateRouter;
import com.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final MongoTemplateRouter templateRouter;

    @Autowired
    public ReportService(MongoTemplateRouter templateRouter) {
        this.templateRouter = templateRouter;
    }

    public List<Map> avgSalaryByDepartment() {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
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
        AggregationResults<Map> results = t.aggregate(aggregation, "employees", Map.class);
        return results.getMappedResults();
    }

    public List<Employee> employeesWithSalaryGreaterThan(double minSalary) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        Query query = Query.query(Criteria.where("salary").gt(minSalary));
        return t.find(query, Employee.class);
    }

    public List<Map> countByPosition() {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("position").count().as("count"),
                Aggregation.project("count").and("_id").as("position")
        );
        AggregationResults<Map> results = t.aggregate(aggregation, "employees", Map.class);
        return results.getMappedResults();
    }

}
