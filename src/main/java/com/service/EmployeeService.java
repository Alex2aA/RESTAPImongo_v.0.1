package com.service;

import com.config.MongoTemplateRouter;
import com.model.Employee;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final MongoTemplateRouter templateRouter;

    @Autowired
    public EmployeeService(MongoTemplateRouter templateRouter) {
        this.templateRouter = templateRouter;
    }

    public List<Employee> getAll() {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.findAll(Employee.class);
    }

    public Employee getById(ObjectId id) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.findById(id, Employee.class);
    }

    public Employee create(Employee employee) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.save(employee);
    }

    public Employee update(ObjectId id, Employee employee) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        employee.setId(id);
        return t.save(employee);
    }

    public void delete(ObjectId id) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        t.remove(Query.query(Criteria.where("_id").is(id)), Employee.class);
    }

}
