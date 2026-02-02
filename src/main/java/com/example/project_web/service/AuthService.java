package com.example.project_web.service;

import com.example.project_web.dto.JwtResponse;
import com.example.project_web.dto.LoginRequest;
import com.example.project_web.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    ResponseEntity<?> registerUser(RegisterRequest signUpRequest);
}
