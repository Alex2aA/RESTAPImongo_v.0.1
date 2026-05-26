package com.service;

import com.config.MongoTemplateRouter;
import com.model.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final MongoTemplateRouter templateRouter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(MongoTemplateRouter templateRouter, PasswordEncoder passwordEncoder) {
        this.templateRouter = templateRouter;
        this.passwordEncoder = passwordEncoder;
    }

    public List<SystemUser> getAllUsers() {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        return t.findAll(SystemUser.class);
    }

    public SystemUser getUserByLogin(String login) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        Query query = Query.query(Criteria.where("login").is(login));
        return t.findOne(query, SystemUser.class);
    }

    public boolean createUser(SystemUser user) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        Query query = Query.query(Criteria.where("login").is(user.getLogin()));
        if (t.exists(query, SystemUser.class)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        t.save(user);
        return true;
    }

    public boolean deleteUser(String login) {
        MongoTemplate t = templateRouter.getTemplateForCurrentUser();
        Query query = Query.query(Criteria.where("login").is(login));
        if (!t.exists(query, SystemUser.class)) {
            return false;
        }
        t.remove(query, SystemUser.class);
        return true;
    }

    public void initDefaultUsers() {
        MongoTemplate adminTemplate = templateRouter.getAdminTemplate();
        if (adminTemplate.count(new Query(), SystemUser.class) == 0) {
            SystemUser reader = new SystemUser();
            reader.setLogin("reader");
            reader.setPassword(passwordEncoder.encode("reader123"));
            reader.setRole("READER");
            adminTemplate.save(reader);

            SystemUser editor = new SystemUser();
            editor.setLogin("editor");
            editor.setPassword(passwordEncoder.encode("editor123"));
            editor.setRole("EDITOR");
            adminTemplate.save(editor);

            SystemUser admin = new SystemUser();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            adminTemplate.save(admin);

            System.out.println("Пользователи созданы с BCrypt-паролями.");
        }
    }

}
