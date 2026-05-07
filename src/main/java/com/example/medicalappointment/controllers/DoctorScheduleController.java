package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.*;
import com.example.medicalappointment.entities.*;
import com.example.medicalappointment.services.*;
import com.example.medicalappointment.repositories.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;
    private final UserService userService;
    private final DoctorRepository doctorRepository;

    public DoctorScheduleController(DoctorScheduleService scheduleService,
                                    UserService userService,
                                    DoctorRepository doctorRepository) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.doctorRepository = doctorRepository;
    }

    // НОВЫЙ МЕТОД: Специально для кнопки "Открыть смену"
    @PostMapping("/me/generate-slots")
    public ResponseEntity<String> generateMySlots(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Doctor doctor = doctorRepository.findByUserId(user.getId()).orElseThrow();

        scheduleService.generateSlots(doctor.getId());
        return ResponseEntity.ok("Смены открыты успешно");
    }

    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<AppointmentSlotDto>> getAvailableSlots(@PathVariable Long doctorId) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(doctorId));
    }

    @GetMapping("/{doctorId}/slots/by-date")
    public ResponseEntity<List<AppointmentSlotDto>> getSlotsByDate(
            @PathVariable Long doctorId,
            @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);

        return ResponseEntity.ok(
                scheduleService.getSlotsByDate(doctorId, localDate)
        );
    }

    @DeleteMapping("/me/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId,
                                           Authentication auth) {

        scheduleService.deleteSlot(slotId, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/slots/{slotId}")
    public ResponseEntity<AppointmentSlotDto> updateSlot(
            @PathVariable Long slotId,
            @RequestBody SlotUpdateDto dto,
            Authentication auth) {

        AppointmentSlot updated = scheduleService
                .updateSlot(slotId, dto, auth.getName());

        return ResponseEntity.ok(mapToDto(updated));
    }

    @PostMapping("/me/create-slots-from-shift")
    public ResponseEntity<List<AppointmentSlotDto>> createSlotsFromShift(
            @RequestBody SlotCreationRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        scheduleService.createSlotsForShift(doctor.getId(), request);

        // Возвращаем созданные слоты
        List<AppointmentSlotDto> slots = scheduleService.getSlotsByDate(doctor.getId(), request.getDate());
        return ResponseEntity.ok(slots);
    }

    // Обновите mapToDto метод
    private AppointmentSlotDto mapToDto(AppointmentSlot slot) {
        AppointmentSlotDto dto = new AppointmentSlotDto();
        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        dto.setPrice(slot.getPrice()); // ✅ Добавляем цену
        return dto;
    }

    /*private AppointmentSlotDto mapToDto(AppointmentSlot slot) {

        AppointmentSlotDto dto = new AppointmentSlotDto();

        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());

        return dto;
    }*/
}