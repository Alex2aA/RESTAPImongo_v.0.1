package com.service;

import com.model.SystemUser;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        SystemUser systemUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
        return new User(
                systemUser.getLogin(),
                systemUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + systemUser.getRole()))
        );
    }
}