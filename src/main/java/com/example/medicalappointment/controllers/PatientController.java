package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.PatientProfileDto;
import com.example.medicalappointment.services.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/profile")
    public ResponseEntity<PatientProfileDto> getPatientProfile(Authentication auth) {
        String username = auth.getName();
        PatientProfileDto profile = patientService.getPatientProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<PatientProfileDto> updatePatientProfile(
            Authentication auth,
            @RequestBody PatientProfileDto profileDto) {
        String username = auth.getName();
        PatientProfileDto updatedProfile = patientService.updatePatientProfile(username, profileDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
