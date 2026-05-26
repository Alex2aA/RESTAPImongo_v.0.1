package com.service;

import com.config.MongoTemplateRouter;
import com.model.Department;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {

    private final MongoTemplateRouter templateRouter;

    @Autowired
    public DepartmentService(MongoTemplateRouter templateRouter) {
        this.templateRouter = templateRouter;
    }

    public List<Department> getAll() {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.findAll(Department.class);
    }

    public Department getById(ObjectId id) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.findById(id, Department.class);
    }

    public Department create(Department department) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.save(department);
    }

    public void delete(ObjectId id) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        t.remove(Query.query(Criteria.where("_id").is(id)), Department.class);
    }

}
