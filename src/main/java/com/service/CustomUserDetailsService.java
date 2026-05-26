package com.service;

import com.config.MongoTemplateRouter;
import com.model.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MongoTemplateRouter templateRouter;

    @Autowired
    public CustomUserDetailsService(MongoTemplateRouter templateRouter) {
        this.templateRouter = templateRouter;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        MongoTemplate adminTemplate = templateRouter.getAdminTemplate();
        Query query = Query.query(Criteria.where("login").is(login));
        SystemUser systemUser = adminTemplate.findOne(query, SystemUser.class);
        if (systemUser == null) {
            throw new UsernameNotFoundException("User not found: " + login);
        }
        return new User(
                systemUser.getLogin(),
                systemUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + systemUser.getRole()))
        );
    }

}
