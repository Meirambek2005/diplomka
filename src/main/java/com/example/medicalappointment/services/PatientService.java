package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.PatientProfileDto;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PatientService {

    private final UserRepository userRepository;

    public PatientService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PatientProfileDto getPatientProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));
        return mapToDto(user);
    }

    @Transactional
    public PatientProfileDto updatePatientProfile(String username, PatientProfileDto profileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        // Обновляем все поля
        if (profileDto.getFirstName() != null) {
            user.setFirstName(profileDto.getFirstName());
        }
        if (profileDto.getLastName() != null) {
            user.setLastName(profileDto.getLastName());
        }
        if (profileDto.getFullName() != null) {
            user.setFullName(profileDto.getFullName());
        } else if (profileDto.getFirstName() != null && profileDto.getLastName() != null) {
            user.setFullName(profileDto.getFirstName() + " " + profileDto.getLastName());
        }
        if (profileDto.getEmail() != null) {
            user.setEmail(profileDto.getEmail());
        }
        if (profileDto.getPhone() != null) {
            user.setPhone(profileDto.getPhone());
        }
        if (profileDto.getIin() != null) {
            user.setIin(profileDto.getIin());
        }
        if (profileDto.getBirthDate() != null && !profileDto.getBirthDate().isEmpty()) {
            user.setBirthDate(String.valueOf(LocalDate.parse(profileDto.getBirthDate())));
        }
        if (profileDto.getGender() != null) {
            user.setGender(profileDto.getGender());
        }
        if (profileDto.getAddress() != null) {
            user.setAddress(profileDto.getAddress());
        }
        if (profileDto.getPolicyNumber() != null) {
            user.setPolicyNumber(profileDto.getPolicyNumber());
        }
        if (profileDto.getEmergencyContact() != null) {
            user.setEmergencyContact(profileDto.getEmergencyContact());
        }
        if (profileDto.getEmergencyPhone() != null) {
            user.setEmergencyPhone(profileDto.getEmergencyPhone());
        }

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    // В PatientService.java добавьте:
    public PatientProfileDto getPatientProfileById(Long patientId, String doctorUsername) {
        // Проверяем, что пользователь - врач
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new RuntimeException("Только врачи могут просматривать профили пациентов");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        return mapToDto(patient);
    }



    private PatientProfileDto mapToDto(User user) {
        PatientProfileDto dto = new PatientProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIin(user.getIin());
        dto.setBirthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : "");
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setPolicyNumber(user.getPolicyNumber());
        dto.setEmergencyContact(user.getEmergencyContact());
        dto.setEmergencyPhone(user.getEmergencyPhone());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}