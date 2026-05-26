package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MongoTemplateRouter {

    private final MongoTemplate readerMongoTemplate;
    private final MongoTemplate editorMongoTemplate;
    private final MongoTemplate adminMongoTemplate;

    @Autowired
    public MongoTemplateRouter(
            @Qualifier("readerMongoTemplate") MongoTemplate readerMongoTemplate,
            @Qualifier("editorMongoTemplate") MongoTemplate editorMongoTemplate,
            @Qualifier("adminMongoTemplate") MongoTemplate adminMongoTemplate) {
        this.readerMongoTemplate = readerMongoTemplate;
        this.editorMongoTemplate = editorMongoTemplate;
        this.adminMongoTemplate = adminMongoTemplate;
    }

    public MongoTemplate getTemplateForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return readerMongoTemplate;
        }
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        switch (role) {
            case "ROLE_ADMIN":
                return adminMongoTemplate;
            case "ROLE_EDITOR":
                return editorMongoTemplate;
            default:
                return readerMongoTemplate;
        }
    }

    public MongoTemplate getAdminTemplate() {
        return adminMongoTemplate;
    }

}
