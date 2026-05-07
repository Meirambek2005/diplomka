package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.AppointmentDto;
import com.example.medicalappointment.dto.SlotCreationRequest;
import com.example.medicalappointment.entities.*;
import com.example.medicalappointment.repositories.AppointmentHistoryRepository;
import com.example.medicalappointment.repositories.AppointmentSlotRepository;
import com.example.medicalappointment.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class AppointmentService {

    private final AppointmentSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final AppointmentHistoryRepository historyRepository;

    public AppointmentService(AppointmentSlotRepository slotRepository,
                              UserRepository userRepository,
                              AppointmentHistoryRepository historyRepository) {
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    // Получение всех записей текущего пользователя (и активные, и отмененные)
    public List<AppointmentDto> getMyAppointments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<AppointmentSlot> slots = slotRepository.findAllByPatientId(currentUser.getId());

        return slots.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDto cancelSlot(Long slotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        if (slot.getStatus() != SlotStatus.BOOKED) {
            throw new RuntimeException("Запись не активна");
        }

        if (slot.getPatient() == null || !slot.getPatient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Можно отменять только свою запись");
        }

        if (slot.getStartTime().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RuntimeException("Отмена невозможна менее чем за 2 часа");
        }

        // Сохраняем в историю
        saveToHistory(slot, "CANCELLED");

        // Очищаем слот для других
        slot.setStatus(SlotStatus.FREE);
        slot.setPatient(null);
        slot.setSymptoms(null);
        slot.setDiagnosis(null);
        slot.setRecommendations(null);
        slot.setMeetingLink(null);

        return mapToDto(slotRepository.save(slot));
    }

    @Transactional
    public AppointmentDto completeAppointment(Long slotId, AppointmentDto resultDto) {
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        slot.setSymptoms(resultDto.getSymptoms());
        slot.setDiagnosis(resultDto.getDiagnosis());
        slot.setRecommendations(resultDto.getRecommendations());
        slot.setStatus(SlotStatus.COMPLETED);
        slot.setCompletedAt(LocalDateTime.now());

        saveToHistory(slot, "COMPLETED");

        return mapToDto(slotRepository.save(slot));
    }

    private void saveToHistory(AppointmentSlot slot, String status) {
        AppointmentHistory history = new AppointmentHistory();
        history.setOriginalSlotId(slot.getId());
        history.setPatient(slot.getPatient());
        history.setDoctor(slot.getDoctor());
        history.setStartTime(slot.getStartTime());
        history.setEndTime(slot.getEndTime());
        history.setStatus(status);
        history.setActionTimestamp(LocalDateTime.now());
        history.setSymptoms(slot.getSymptoms());
        history.setDiagnosis(slot.getDiagnosis());
        history.setRecommendations(slot.getRecommendations());

        historyRepository.save(history);
    }

    public List<AppointmentDto> getPatientHistory(Long patientId) {
        return historyRepository.findAllByPatientIdOrderByStartTimeDesc(patientId)
                .stream()
                .map(this::mapHistoryToDto)
                .collect(Collectors.toList());
    }

    private AppointmentDto mapHistoryToDto(AppointmentHistory history) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(history.getOriginalSlotId());
        dto.setStartTime(history.getStartTime());
        dto.setEndTime(history.getEndTime());
        dto.setStatus(history.getStatus());
        dto.setSymptoms(history.getSymptoms());
        dto.setDiagnosis(history.getDiagnosis());
        dto.setRecommendations(history.getRecommendations());

        if (history.getDoctor() != null) {
            dto.setDoctorId(history.getDoctor().getId());
            dto.setDoctorFullName(history.getDoctor().getFullName());
            dto.setDoctorSpecialty(history.getDoctor().getSpecialty());
            dto.setClinicAddress(history.getDoctor().getClinicAddress());
        }
        return dto;
    }

    @Transactional
    public AppointmentDto bookSlot(Long slotId, Boolean isOnline) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Слот не найден"));

        if (slot.getStatus() != SlotStatus.FREE) {
            throw new RuntimeException("Слот уже занят");
        }

        slot.setStatus(SlotStatus.BOOKED);
        slot.setPatient(patient);
        slot.setIsOnline(isOnline);

        if (Boolean.TRUE.equals(isOnline)) {
            slot.setMeetingLink("https://meet.jit.si/medical-" + UUID.randomUUID());
        }

        return mapToDto(slotRepository.save(slot));
    }

    public List<AppointmentDto> getDoctorAppointments(Long doctorId) {
        List<AppointmentSlot> slots = slotRepository.findAllByDoctorId(doctorId);

        return slots.stream()
                .filter(slot -> slot.getStatus() != SlotStatus.FREE)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    // ГЛАВНЫЙ МЕТОД mapToDto - ИСПРАВЛЕН
    public AppointmentDto mapToDto(AppointmentSlot slot) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus().name());
        dto.setIsOnline(slot.getIsOnline());
        dto.setMeetingLink(slot.getMeetingLink());
        dto.setSymptoms(slot.getSymptoms());
        dto.setDiagnosis(slot.getDiagnosis());
        dto.setRecommendations(slot.getRecommendations());
        dto.setCompletedAt(slot.getCompletedAt());
        dto.setPrice(slot.getPrice());

        // Устанавливаем информацию о пациенте
        if (slot.getPatient() != null) {
            dto.setPatientFullName(slot.getPatient().getFullName());
            dto.setPatientPhone(slot.getPatient().getPhone());
        }

        if (slot.getDoctor() != null) {
            Doctor doctor = slot.getDoctor();
            dto.setDoctorId(doctor.getId());
            dto.setDoctorFullName(doctor.getFullName());
            dto.setDoctorSpecialty(doctor.getSpecialty());
            dto.setDoctorPhone(doctor.getPhone());
            dto.setClinicAddress(doctor.getClinicAddress());
        }

        return dto;
    }


}