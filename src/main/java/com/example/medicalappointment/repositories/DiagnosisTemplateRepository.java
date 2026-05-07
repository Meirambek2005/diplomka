package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.DiagnosisTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisTemplateRepository extends JpaRepository<DiagnosisTemplate, Long> {

    List<DiagnosisTemplate> findByDoctorId(Long doctorId);
}