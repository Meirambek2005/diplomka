package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.*;
import com.example.medicalappointment.entities.AppointmentStatus;
import com.example.medicalappointment.entities.SlotStatus;
import com.example.medicalappointment.repositories.AppointmentRepository;
import com.example.medicalappointment.repositories.ReviewRepository;

import com.example.medicalappointment.entities.Doctor;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.repositories.DoctorRepository;
import com.example.medicalappointment.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         AppointmentRepository appointmentRepository,
                         ReviewRepository reviewRepository) {

        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.reviewRepository = reviewRepository;
    }

    // 📌 Найти профиль врача по userId
    public Doctor findByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Профиль врача не найден"));
    }

    // 📌 Получить всех врачей
    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 🔍 НОВЫЙ ПОИСК (главный метод)
    public List<DoctorResponseDto> searchDoctors(String specialty,
                                                 String city,
                                                 Double rating) {

        if (specialty == null) specialty = "";
        if (city == null) city = "";
        if (rating == null) rating = 0.0;

        // 🔥 НОРМАЛИЗАЦИЯ
        specialty = specialty.trim();
        city = city.trim();

        return doctorRepository
                .findBySpecialtyContainingIgnoreCaseAndCityContainingIgnoreCaseAndRatingGreaterThanEqual(
                        specialty,
                        city,
                        rating
                )
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 📌 Получить врача по ID
    public DoctorResponseDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Врач не найден"));
        return mapToDto(doctor);
    }

    // 📌 Создать профиль врача
    public DoctorResponseDto createDoctorProfile(@Valid DoctorCreateDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Не авторизован");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // ✅ правильная проверка роли
        if (!user.getRole().name().equals("DOCTOR")) {
            throw new RuntimeException("Только DOCTOR может создать профиль");
        }

        if (doctorRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("Профиль уже существует");
        }

        Doctor doctor = new Doctor();

        doctor.setUserId(user.getId());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setRating(dto.getRating() != null ? dto.getRating() : 0.0);

        // 🔥 важно для поиска
        doctor.setCity(dto.getCity());

        Doctor saved = doctorRepository.save(doctor);

        return mapToDto(saved);
    }

    // 📊 СТАТИСТИКА ВРАЧА
    public DoctorStatsDto getDoctorStats(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Профиль врача не найден"));

        Long doctorId = doctor.getId();

        // 📊 завершённые приёмы
        Long completed = appointmentRepository
                .countByDoctorIdAndStatus(doctorId, AppointmentStatus.COMPLETED.COMPLETED);

        // 👥 уникальные пациенты
        Long patients = appointmentRepository
                .countUniquePatients(doctorId);

        // ⭐ средний рейтинг
        Double avgRating = reviewRepository.getAverageRating(doctorId);

        if (avgRating == null) avgRating = 0.0;

        DoctorStatsDto dto = new DoctorStatsDto();
        dto.setCompletedAppointments(completed);
        dto.setTotalPatients(patients);
        dto.setAverageRating(avgRating);

        return dto;
    }

    public List<AppointmentStatsDto> getAppointmentsStats(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Object[]> results = appointmentRepository.getAppointmentsStats(doctor.getId());

        return results.stream().map(obj -> {
            AppointmentStatsDto dto = new AppointmentStatsDto();
            dto.setDate(obj[0].toString());
            dto.setCount((Long) obj[1]);
            return dto;
        }).toList();
    }

    public List<HourStatsDto> getHourlyStats(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return appointmentRepository.getHourlyStats(doctor.getId())
                .stream()
                .map(obj -> {
                    HourStatsDto dto = new HourStatsDto();
                    dto.setHour((Integer) obj[0]);
                    dto.setCount((Long) obj[1]);
                    return dto;
                }).toList();
    }

    // 📌 МАППИНГ
    private DoctorResponseDto mapToDto(Doctor doctor) {

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new RuntimeException("User не найден"));

        DoctorResponseDto dto = new DoctorResponseDto();

        dto.setId(doctor.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());

        dto.setSpecialty(doctor.getSpecialty());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setRating(doctor.getRating());
        dto.setCity(doctor.getCity());

        return dto;
    }
}