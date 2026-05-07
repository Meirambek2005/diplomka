package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.MedicalRecordDto;
import com.example.medicalappointment.services.MedicalRecordService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    //  ПОЛУЧИТЬ СВОЮ КАРТУ
    @GetMapping("/me")
    public MedicalRecordDto getMyRecord(Authentication auth) {
        return service.getMyRecord(auth.getName());
    }

    //  СОЗДАТЬ / ОБНОВИТЬ
    @PostMapping("/me")
    public MedicalRecordDto save(Authentication auth,
                                 @RequestBody MedicalRecordDto dto) {
        return service.saveOrUpdate(auth.getName(), dto);
    }
}
