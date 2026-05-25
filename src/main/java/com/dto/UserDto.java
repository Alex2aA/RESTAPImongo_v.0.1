package com.dto;

import lombok.Data;

@Data
public class UserDto {
    private String login;
    private String password;
    private String role;
}