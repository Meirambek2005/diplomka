package com.example.medicalappointment.controllers;

import java.time.LocalDate;
import java.util.*;
import com.example.medicalappointment.dto.AppointmentDto;
import com.example.medicalappointment.dto.SlotCreationRequest;
import com.example.medicalappointment.entities.*;
import com.example.medicalappointment.repositories.AppointmentSlotRepository;
import com.example.medicalappointment.repositories.ReviewRepository;
import com.example.medicalappointment.repositories.UserRepository;
import com.example.medicalappointment.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Управление записями и обслуживание пациентов")
public class AppointmentController {

    private final AppointmentSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final AppointmentService appointmentService;// Используем сервис для логики


    public AppointmentController(AppointmentSlotRepository slotRepository,
                                 UserRepository userRepository,
                                 AppointmentService appointmentService) { // Добавили имя переменной
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
    }

   /* @PostMapping("/book/{slotId}")
    @Operation(summary = "Забронировать слот (Пациент)")
    public ResponseEntity<AppointmentDto> bookSlot(@PathVariable Long slotId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User patient = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Слот не найден"));

        if (slot.getStatus() != SlotStatus.FREE) throw new RuntimeException("Слот занят");

        slot.setStatus(SlotStatus.BOOKED);
        slot.setPatient(patient);
        slotRepository.save(slot);

        return ResponseEntity.ok(appointmentService.mapToDto(slot));
    }*/

    @PostMapping("/book/{slotId}")
    @Operation(summary = "Забронировать слот (Пациент)")
    public ResponseEntity<AppointmentDto> bookSlot(
            @PathVariable Long slotId,
            @RequestParam(defaultValue = "false") Boolean isOnline
    ) {
        return ResponseEntity.ok(
                appointmentService.bookSlot(slotId, isOnline)
        );
    }

   /* @PostMapping("/book/{slotId}")
    @Operation(summary = "Забронировать консультацию (Онлайн или Оффлайн)")
    public ResponseEntity<AppointmentDto> bookSlot(
            @PathVariable Long slotId,
            @RequestParam Boolean isOnline // Убираем дефолтное значение, чтобы фронт явно передавал выбор
    ) {
        return ResponseEntity.ok(
                appointmentService.bookSlot(slotId, isOnline)
        );
    }*/


    // НОВЫЙ МЕТОД: Завершение приема врачом (Блок 2)
    @PutMapping("/{slotId}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Завершить прием и внести диагноз (Врач)")
    public ResponseEntity<AppointmentDto> completeAppointment(
            @PathVariable Long slotId,
            @RequestBody AppointmentDto resultDto) {

        AppointmentDto updated = appointmentService.completeAppointment(slotId, resultDto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{slotId}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable Long slotId) {
        return ResponseEntity.ok(appointmentService.cancelSlot(slotId));
    }
/*
    @GetMapping("/my")
    @Operation(summary = "Активные записи текущего пациента")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments() {
        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        List<AppointmentSlot> slots = slotRepository.findByPatientIdAndStatus(patient.getId(), SlotStatus.BOOKED);
        return ResponseEntity.ok(slots.stream().map(appointmentService::mapToDto).collect(Collectors.toList()));
    }

    @GetMapping("/my/history")
    @Operation(summary = "История всех записей пациента (с диагнозами)")
    public ResponseEntity<Page<AppointmentDto>> getAppointmentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime,desc") String sort,
            @RequestParam(required = false) String status) {

        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        String[] sortParts = sort.split(",");
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<AppointmentSlot> slotsPage;
        if (status != null && !status.isBlank()) {
            slotsPage = slotRepository.findByPatientIdAndStatus(patient.getId(), SlotStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            slotsPage = slotRepository.findByPatientId(patient.getId(), pageable);
        }

        return ResponseEntity.ok(slotsPage.map(appointmentService::mapToDto));
    }*/
    /*@GetMapping("/my")
    @Operation(summary = "Активные записи текущего пациента")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments() {
        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        // ✅ ИСПРАВЛЕНИЕ: Берем ВСЕ слоты пациента (BOOKED + CANCELLED), не только BOOKED
        List<AppointmentSlot> slots = slotRepository.findByPatientId(patient.getId());
        List<AppointmentDto> activeAppointments = slots.stream()
                .filter(slot -> slot.getStatus() == SlotStatus.BOOKED || slot.getStatus() == SlotStatus.CANCELLED)
                .map(appointmentService::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activeAppointments);
    }


    // ✅ ИСТОРИЯ ЗАПИСЕЙ
    @GetMapping("/my/history")
    @Operation(summary = "Полная история всех записей пациента (включая завершенные)")
    public ResponseEntity<List<AppointmentDto>> getMyAppointmentsHistory() {
        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        List<AppointmentDto> historyFromService = appointmentService.getPatientHistory(patient.getId());

        return ResponseEntity.ok(historyFromService);
    }*/

/*
    @GetMapping("/my/history")
    @Operation(summary = "Полная история всех записей пациента (включая завершенные)")
    public ResponseEntity<List<AppointmentDto>> getMyAppointmentsHistory() {
        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        // ✅ ИСПРАВЛЕНИЕ: Берем из истории + текущие слоты пациента
        List<AppointmentDto> historyFromService = appointmentService.getPatientHistory(patient.getId());

        // Добавляем текущие слоты пациента (которые еще не в истории)
        List<AppointmentSlot> currentSlots = slotRepository.findByPatientId(patient.getId());
        List<AppointmentDto> currentSlotsDtos = currentSlots.stream()
                .filter(slot -> slot.getStatus() != SlotStatus.FREE) // Исключаем свободные
                .map(appointmentService::mapToDto)
                .collect(Collectors.toList());

        // ✅ Объединяем и убираем дубликаты по slot ID
        List<AppointmentDto> allAppointments = new ArrayList<>();
        allAppointments.addAll(historyFromService);
        allAppointments.addAll(currentSlotsDtos.stream()
                .filter(dto -> historyFromService.stream().noneMatch(h -> h.getId().equals(dto.getId())))
                .collect(Collectors.toList()));

        return ResponseEntity.ok(allAppointments);
    }*/
@GetMapping("/my/history")
@Operation(summary = "Полная история всех записей пациента (включая завершенные, отмененные и активные)")
public ResponseEntity<List<AppointmentDto>> getMyAppointmentsHistory() {
    User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
            .orElseThrow(() -> new RuntimeException("Пациент не найден"));

    // Получаем историю из AppointmentHistory (завершенные приемы)
    List<AppointmentDto> historyFromService = appointmentService.getPatientHistory(patient.getId());

    // Получаем текущие слоты пациента (активные BOOKED, отмененные CANCELLED)
    List<AppointmentSlot> currentSlots = slotRepository.findAllByPatientId(patient.getId());
    List<AppointmentDto> currentSlotsDtos = currentSlots.stream()
            .filter(slot -> slot.getStatus() == SlotStatus.BOOKED ||
                    slot.getStatus() == SlotStatus.CANCELLED ||
                    slot.getStatus() == SlotStatus.COMPLETED)
            .map(appointmentService::mapToDto)
            .collect(Collectors.toList());

    // Объединяем и убираем дубликаты по ID
    Map<Long, AppointmentDto> uniqueAppointments = new LinkedHashMap<>();

    // Добавляем историю
    for (AppointmentDto dto : historyFromService) {
        uniqueAppointments.put(dto.getId(), dto);
    }

    // Добавляем текущие слоты (перезаписываем если есть дубликаты, чтобы иметь актуальный статус)
    for (AppointmentDto dto : currentSlotsDtos) {
        uniqueAppointments.put(dto.getId(), dto);
    }

    // Преобразуем обратно в список и сортируем по дате (новые сверху)
    List<AppointmentDto> allAppointments = new ArrayList<>(uniqueAppointments.values());
    allAppointments.sort((a, b) -> b.getStartTime().compareTo(a.getStartTime()));

    return ResponseEntity.ok(allAppointments);
}
    // ✅ ИСПРАВЛЕННЫЙ МЕТОД: Получение всех записей пациента
    @GetMapping("/my")
    @Operation(summary = "Все записи текущего пациента (активные и отмененные)")
    public ResponseEntity<List<AppointmentDto>> getMyAppointments() {
        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        // Используем правильный метод репозитория
        List<AppointmentSlot> slots = slotRepository.findAllByPatientId(patient.getId());

        List<AppointmentDto> appointments = slots.stream()
                .map(appointmentService::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointments);
    }

    // ✅ МЕТОД ДЛЯ ПОЛУЧЕНИЯ ЗАПИСИ ПО ID
    @GetMapping("/{id}")
    @Operation(summary = "Получить детали записи по ID")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        AppointmentSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));
        return ResponseEntity.ok(appointmentService.mapToDto(slot));
    }

    /*// ✅ ИСТОРИЯ ЗАПИСЕЙ
    @GetMapping("/my/history")
    @Operation(summary = "Полная история всех записей пациента (включая завершенные)")
    public ResponseEntity<List<AppointmentDto>> getMyAppointmentsHistory() {
        User patient = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        List<AppointmentDto> historyFromService = appointmentService.getPatientHistory(patient.getId());

        return ResponseEntity.ok(historyFromService);
    }*/

    @GetMapping("/{slotId}/online")
    public ResponseEntity<String> getOnlineLink(@PathVariable Long slotId) {

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Слот не найден"));

        if (!Boolean.TRUE.equals(slot.getIsOnline())) {
            return ResponseEntity.badRequest().body("Это не онлайн консультация");
        }

        // защита по времени
        if (slot.getStartTime().minusMinutes(10).isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Слишком рано для входа");
        }

        return ResponseEntity.ok(slot.getMeetingLink());
    }



}
