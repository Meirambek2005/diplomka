package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.DiagnosisTemplateDto;
import com.example.medicalappointment.services.DiagnosisTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class DiagnosisTemplateController {

    private final DiagnosisTemplateService service;

    public DiagnosisTemplateController(DiagnosisTemplateService service) {
        this.service = service;
    }

    //  создать
    @PostMapping
    public ResponseEntity<DiagnosisTemplateDto> create(
            @RequestBody DiagnosisTemplateDto dto,
            Authentication auth) {

        return ResponseEntity.ok(service.create(auth.getName(), dto));
    }

    //  получить свои шаблоны
    @GetMapping("/me")
    public ResponseEntity<List<DiagnosisTemplateDto>> getMy(Authentication auth) {

        return ResponseEntity.ok(service.getMyTemplates(auth.getName()));
    }
}