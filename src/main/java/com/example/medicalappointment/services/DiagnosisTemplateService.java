package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.DiagnosisTemplateDto;
import com.example.medicalappointment.entities.*;
import com.example.medicalappointment.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiagnosisTemplateService {

    private final DiagnosisTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public DiagnosisTemplateService(DiagnosisTemplateRepository templateRepository,
                                    UserRepository userRepository,
                                    DoctorRepository doctorRepository) {
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    // 📌 создать шаблон
    public DiagnosisTemplateDto create(String username, DiagnosisTemplateDto dto) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Doctor doctor = doctorRepository.findByUserId(user.getId()).orElseThrow();

        DiagnosisTemplate template = new DiagnosisTemplate();
        template.setTitle(dto.getTitle());
        template.setDiagnosis(dto.getDiagnosis());
        template.setRecommendations(dto.getRecommendations());
        template.setDoctor(doctor);

        return mapToDto(templateRepository.save(template));
    }

    // 📌 список шаблонов врача
    public List<DiagnosisTemplateDto> getMyTemplates(String username) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Doctor doctor = doctorRepository.findByUserId(user.getId()).orElseThrow();

        return templateRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    // 📌 маппинг
    private DiagnosisTemplateDto mapToDto(DiagnosisTemplate t) {

        DiagnosisTemplateDto dto = new DiagnosisTemplateDto();

        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDiagnosis(t.getDiagnosis());
        dto.setRecommendations(t.getRecommendations());

        return dto;
    }
}