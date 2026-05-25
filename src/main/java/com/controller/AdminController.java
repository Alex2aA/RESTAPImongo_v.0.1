package com.controller;

import com.dto.UserDto;
import com.model.SystemUser;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<SystemUser> listUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public boolean addUser(@RequestBody UserDto userDto) {
        SystemUser user = new SystemUser();
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        return userService.createUser(user);
    }

    @DeleteMapping("/{login}")
    public boolean deleteUser(@PathVariable String login) {
        return userService.deleteUser(login);
    }
}