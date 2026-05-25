package com.service;

import com.model.SystemUser;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<SystemUser> getAllUsers() {
        return userRepository.findAll();
    }

    public SystemUser getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    public boolean createUser(SystemUser user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // BCrypt
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(String login) {
        if (!userRepository.existsByLogin(login)) {
            return false;
        }
        userRepository.deleteByLogin(login);
        return true;
    }

    // Инициализация трёх пользователей по умолчанию
    // service/UserService.java
    public void initDefaultUsers() {
        if (userRepository.count() == 0) {
            SystemUser reader = new SystemUser();
            reader.setLogin("reader");
            reader.setPassword("reader123");          // сырой пароль
            reader.setRole("READER");
            createUser(reader);

            SystemUser editor = new SystemUser();
            editor.setLogin("editor");
            editor.setPassword("editor123");
            editor.setRole("EDITOR");
            createUser(editor);

            SystemUser admin = new SystemUser();
            admin.setLogin("admin");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            createUser(admin);

            System.out.println("Пользователи созданы с BCrypt-паролями.");
        }
    }
}