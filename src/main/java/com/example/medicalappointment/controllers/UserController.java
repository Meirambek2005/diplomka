package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.UserUpdateDto;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Обновление профиля текущего пользователя
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(
            @RequestBody UserUpdateDto dto,
            Authentication authentication) {

        String username = authentication.getName();
        log.info("Received update request for user: {}", username);
        log.info("Update data: fullName={}, email={}, phone={}",
                dto.getFullName(), dto.getEmail(), dto.getPhone());

        User updatedUser = userService.updateProfile(username, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        response.put("user", updatedUser);

        log.info("User updated successfully: {}", updatedUser.getUsername());

        return ResponseEntity.ok(response);
    }

    // Получение текущего пользователя
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}