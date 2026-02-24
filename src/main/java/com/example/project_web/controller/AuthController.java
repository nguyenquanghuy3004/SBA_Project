package com.example.project_web.controller;

import com.example.project_web.dto.LoginRequest;
import com.example.project_web.dto.RegisterRequest;
import com.example.project_web.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
@RestController
    @RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegisterRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }
}
