package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.MedicalRecordDto;
import com.example.medicalappointment.entities.MedicalRecord;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.exception.ApiException;
import com.example.medicalappointment.repositories.MedicalRecordRepository;
import com.example.medicalappointment.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(MedicalRecordRepository recordRepository,
                                UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    // 📌 ПОЛУЧИТЬ КАРТУ
    public MedicalRecordDto getMyRecord(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Пользователь не найден"));

        MedicalRecord record = recordRepository.findByPatientId(user.getId())
                .orElseThrow(() -> new ApiException("Медкарта не найдена"));

        return mapToDto(record);
    }

    // 📌 СОЗДАТЬ/ОБНОВИТЬ
    public MedicalRecordDto saveOrUpdate(String username, MedicalRecordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Пользователь не найден"));

        MedicalRecord record = recordRepository.findByPatientId(user.getId())
                .orElse(new MedicalRecord());

        record.setPatient(user);
        record.setBloodType(dto.getBloodType());
        record.setAllergies(dto.getAllergies());
        record.setChronicDiseases(dto.getChronicDiseases());
        record.setNotes(dto.getNotes());
        record.setMedications(dto.getMedications());
        record.setSurgeries(dto.getSurgeries());
        record.setFamilyHistory(dto.getFamilyHistory());

        if (record.getId() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        record.setUpdatedAt(LocalDateTime.now());

        return mapToDto(recordRepository.save(record));
    }

    // 📌 ПОЛУЧИТЬ МЕДКАРТУ ПАЦИЕНТА ДЛЯ ВРАЧА
    public MedicalRecordDto getPatientMedicalRecord(Long patientId, String doctorUsername) {
        // Проверяем, что пользователь - врач
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new RuntimeException("Только врачи могут просматривать медицинские карты пациентов");
        }

        // Проверяем, что пациент существует
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Пациент не найден с ID: " + patientId));

        MedicalRecord medicalRecord = recordRepository.findByPatientId(patientId)
                .orElse(new MedicalRecord());

        if (medicalRecord.getId() == null) {
            medicalRecord.setPatient(patient);
            medicalRecord.setCreatedAt(LocalDateTime.now());
            medicalRecord = recordRepository.save(medicalRecord);
        }

        return mapToDto(medicalRecord);
    }

    // 📌 ПОЛУЧИТЬ СВОЮ МЕДКАРТУ
    public MedicalRecordDto getMyMedicalCard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        MedicalRecord medicalRecord = recordRepository.findByPatientId(user.getId())
                .orElse(new MedicalRecord());

        if (medicalRecord.getId() == null) {
            medicalRecord.setPatient(user);
            medicalRecord.setCreatedAt(LocalDateTime.now());
            medicalRecord = recordRepository.save(medicalRecord);
        }

        return mapToDto(medicalRecord);
    }

    // 📌 ОБНОВИТЬ СВОЮ МЕДКАРТУ
    public MedicalRecordDto updateMyMedicalCard(String username, MedicalRecordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        MedicalRecord medicalRecord = recordRepository.findByPatientId(user.getId())
                .orElse(new MedicalRecord());

        if (medicalRecord.getId() == null) {
            medicalRecord.setPatient(user);
            medicalRecord.setCreatedAt(LocalDateTime.now());
        }

        medicalRecord.setBloodType(dto.getBloodType());
        medicalRecord.setAllergies(dto.getAllergies());
        medicalRecord.setChronicDiseases(dto.getChronicDiseases());
        medicalRecord.setMedications(dto.getMedications());
        medicalRecord.setSurgeries(dto.getSurgeries());
        medicalRecord.setFamilyHistory(dto.getFamilyHistory());
        medicalRecord.setNotes(dto.getNotes());
        medicalRecord.setUpdatedAt(LocalDateTime.now());

        return mapToDto(recordRepository.save(medicalRecord));
    }

    // 📌 МАППИНГ
    private MedicalRecordDto mapToDto(MedicalRecord record) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(record.getId());
        dto.setBloodType(record.getBloodType());
        dto.setAllergies(record.getAllergies());
        dto.setChronicDiseases(record.getChronicDiseases());
        dto.setMedications(record.getMedications());
        dto.setSurgeries(record.getSurgeries());
        dto.setFamilyHistory(record.getFamilyHistory());
        dto.setNotes(record.getNotes());

        if (record.getPatient() != null) {
            dto.setPatientId(record.getPatient().getId());
            dto.setPatientName(record.getPatient().getFullName());
        }

        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());

        return dto;
    }
}