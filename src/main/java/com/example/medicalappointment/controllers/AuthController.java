package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.AuthResponse;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        log.info("Register request for user: {}", user.getUsername());
        AuthResponse response = authService.register(user);
        return ResponseEntity.ok(response);
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.username);
        AuthResponse response = authService.login(request.username, request.password);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        log.info("Getting current user: {}", username);
        User currentUser = authService.getUserByUsername(username);
        return ResponseEntity.ok(currentUser);
    }
}