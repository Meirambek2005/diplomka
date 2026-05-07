package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.*;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.entities.Doctor;
import com.example.medicalappointment.repositories.DoctorRepository; // Нужно добавить
import com.example.medicalappointment.services.AppointmentService;
import com.example.medicalappointment.services.DoctorService;
import com.example.medicalappointment.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final DoctorRepository doctorRepository; // Добавили репозиторий

    public DoctorController(DoctorService doctorService,
                            AppointmentService appointmentService,
                            UserService userService,
                            DoctorRepository doctorRepository) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/me/appointments")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ:
        // Находим профиль доктора (где id=1), используя userId (который равен 10)
        Doctor doctorProfile = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Медицинский профиль врача не найден"));

        // Теперь передаем ID профиля врача (ID=1), а не ID пользователя (ID=10)
        List<AppointmentDto> appointments = appointmentService.getDoctorAppointments(doctorProfile.getId());

        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponseDto>> searchDoctors(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double rating
    ) {
        return ResponseEntity.ok(
                doctorService.searchDoctors(specialty, city, rating)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/me/stats")
    public ResponseEntity<DoctorStatsDto> getMyStats(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                doctorService.getDoctorStats(auth.getName())
        );
    }

    @GetMapping("/me/stats/chart")
    public ResponseEntity<List<AppointmentStatsDto>> getChart(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                doctorService.getAppointmentsStats(auth.getName())
        );
    }

    @GetMapping("/me/stats/hourly")
    public ResponseEntity<List<HourStatsDto>> getHourly(Authentication auth) {

        if (auth == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                doctorService.getHourlyStats(auth.getName())
        );
    }

    /*@PostMapping
    public ResponseEntity<DoctorResponseDto> createDoctorProfile(
            @RequestBody @Valid DoctorCreateDto dto,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.status(201).body(doctorService.createDoctorProfile(dto));
    }*/
    @PostMapping
    public ResponseEntity<DoctorResponseDto> createDoctorProfile(
            @RequestBody @Valid DoctorCreateDto dto,
            Authentication authentication) {

        // Проверка роли (можно оставить в сервисе, но дублируем для безопасности)
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        DoctorResponseDto created = doctorService.createDoctorProfile(dto);
        return ResponseEntity.status(201).body(created);
    }

}