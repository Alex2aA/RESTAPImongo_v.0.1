package com.controller;

import com.config.JwtUtil;
import com.dto.AuthResponse;
import com.dto.LoginRequest;
import com.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
        String token = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new AuthResponse(token, role, request.getLogin());
    }
}