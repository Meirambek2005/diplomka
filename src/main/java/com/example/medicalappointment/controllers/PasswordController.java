package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.*;
import com.example.medicalappointment.services.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    //  forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        passwordService.forgotPassword(request);
        return ResponseEntity.ok("Токен отправлен (смотри консоль)");

    }


    //  reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {

        passwordService.resetPassword(request);
        return ResponseEntity.ok("Пароль успешно обновлен");
    }

}