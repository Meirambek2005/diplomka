package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.MedicalFileDto;
import com.example.medicalappointment.services.MedicalFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medical-files")
public class MedicalFileController {

    private final MedicalFileService service;

    public MedicalFileController(MedicalFileService service) {
        this.service = service;
    }

    @PostMapping("/{slotId}")
    public ResponseEntity<MedicalFileDto> uploadFile(
            @PathVariable Long slotId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            Authentication auth) {

        String username = auth.getName();
        MedicalFileDto result = service.addFile(slotId, username, file, type);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<List<MedicalFileDto>> getFiles(@PathVariable Long slotId) {
        return ResponseEntity.ok(service.getFilesByAppointment(slotId));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId, Authentication auth) {
        String username = auth.getName();
        service.deleteFile(fileId, username);
        return ResponseEntity.noContent().build();
    }
}