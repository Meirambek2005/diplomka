package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.AppointmentSlotDto;
import com.example.medicalappointment.dto.DoctorScheduleDto;
import com.example.medicalappointment.dto.SlotCreationRequest;
import com.example.medicalappointment.dto.SlotUpdateDto;
import com.example.medicalappointment.entities.*;
import com.example.medicalappointment.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final AppointmentSlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorScheduleService(DoctorScheduleRepository scheduleRepository,
                                 AppointmentSlotRepository slotRepository,
                                 UserRepository userRepository,
                                 DoctorRepository doctorRepository) {
        this.scheduleRepository = scheduleRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Генерация слотов на определенную дату с удалением существующих
     */
    @Transactional
    public void generateSlotsForDate(Long doctorId, LocalDate date, DoctorSchedule schedule) {
        // 1. Удаляем существующие слоты на эту дату
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<AppointmentSlot> existingSlots = slotRepository.findByDoctorIdAndStartTimeBetween(
                doctorId, startOfDay, endOfDay
        );

        if (!existingSlots.isEmpty()) {
            slotRepository.deleteAll(existingSlots);
            System.out.println("Удалено существующих слотов на " + date + ": " + existingSlots.size());
        }

        // 2. Создаем новые слоты
        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        int duration = schedule.getSlotDurationMinutes();

        // Перерыв на обед (по умолчанию 13:00 - 14:00)
        LocalTime breakStart = LocalTime.of(13, 0);
        LocalTime breakEnd = LocalTime.of(14, 0);

        LocalTime current = start;
        int slotCount = 0;

        while (current.isBefore(end)) {
            LocalTime slotEnd = current.plusMinutes(duration);

            // Проверяем, не попадает ли слот на обеденный перерыв
            boolean isBreak = !(slotEnd.isBefore(breakStart) || current.isAfter(breakEnd));

            if (!isBreak && (slotEnd.isBefore(end) || slotEnd.equals(end))) {
                AppointmentSlot slot = new AppointmentSlot();
                slot.setDoctor(schedule.getDoctor());
                slot.setStartTime(LocalDateTime.of(date, current));
                slot.setEndTime(LocalDateTime.of(date, slotEnd));
                slot.setStatus(SlotStatus.FREE);
                slotRepository.save(slot);
                slotCount++;
            }

            current = current.plusMinutes(duration);
        }

        System.out.println("Создано слотов на " + date + ": " + slotCount);
    }

    /**
     * Генерация слотов на 7 дней вперед
     */
    @Transactional
    public void generateSlots(Long doctorId) {
        DoctorSchedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Расписание не настроено. Сначала создайте график работы."));

        LocalDate today = LocalDate.now();
        int generatedCount = 0;

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);

            // Проверяем, рабочий ли день
            if (schedule.getWorkingDays() == null ||
                    schedule.getWorkingDays().contains(date.getDayOfWeek())) {

                generateSlotsForDate(doctorId, date, schedule);
                generatedCount++;
            }
        }

        System.out.println("Генерация завершена. Обработано дней: " + generatedCount);
    }

    /**
     * Генерация слотов на конкретную дату (для ручного вызова)
     */
    @Transactional
    public void generateSlotsForSpecificDate(Long doctorId, LocalDate date) {
        DoctorSchedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Расписание не настроено"));

        generateSlotsForDate(doctorId, date, schedule);
    }

    public List<AppointmentSlotDto> getAvailableSlots(Long doctorId) {
        LocalDateTime now = LocalDateTime.now();
        return slotRepository.findByDoctorIdAndStartTimeBetween(doctorId, now, now.plusDays(7))
                .stream()
                .filter(slot -> slot.getStatus() == SlotStatus.FREE)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentSlotDto> getSlotsByDate(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        return slotRepository.findByDoctorIdAndStartTimeBetween(doctorId, startOfDay, endOfDay)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
/*
    private AppointmentSlotDto toDto(AppointmentSlot slot) {
        AppointmentSlotDto dto = new AppointmentSlotDto();
        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        return dto;
    }*/

    @Transactional
    public void deleteSlot(Long slotId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Это не ваш слот");
        }

        if (slot.getStatus() != SlotStatus.FREE) {
            throw new RuntimeException("Нельзя удалить занятый слот");
        }

        slotRepository.delete(slot);
    }
/*
    @Transactional
    public AppointmentSlot updateSlot(Long slotId,
                                      SlotUpdateDto dto,
                                      String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Это не ваш слот");
        }

        if (slot.getStatus() != SlotStatus.FREE) {
            throw new RuntimeException("Нельзя редактировать занятый слот");
        }

        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());

        return slotRepository.save(slot);
    }*/

    @Transactional
    public DoctorScheduleDto createOrUpdateSchedule(Long doctorId, DoctorScheduleDto dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorSchedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElse(new DoctorSchedule());

        schedule.setDoctor(doctor);
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        schedule.setWorkingDays(dto.getWorkingDays());

        scheduleRepository.save(schedule);

        //  После сохранения расписания, генерируем слоты
        generateSlots(doctorId);

        return dto;
    }

    @Transactional
    public void generateSlotsForDate(Long doctorId, LocalDate date, DoctorSchedule schedule, Integer defaultPrice) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<AppointmentSlot> existingSlots = slotRepository.findByDoctorIdAndStartTimeBetween(
                doctorId, startOfDay, endOfDay
        );

        if (!existingSlots.isEmpty()) {
            slotRepository.deleteAll(existingSlots);
            System.out.println("Удалено существующих слотов на " + date + ": " + existingSlots.size());
        }

        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        int duration = schedule.getSlotDurationMinutes();

        LocalTime breakStart = LocalTime.of(13, 0);
        LocalTime breakEnd = LocalTime.of(14, 0);

        LocalTime current = start;
        int slotCount = 0;

        while (current.isBefore(end)) {
            LocalTime slotEnd = current.plusMinutes(duration);

            boolean isBreak = !(slotEnd.isBefore(breakStart) || current.isAfter(breakEnd));

            if (!isBreak && (slotEnd.isBefore(end) || slotEnd.equals(end))) {
                AppointmentSlot slot = new AppointmentSlot();
                slot.setDoctor(schedule.getDoctor());
                slot.setStartTime(LocalDateTime.of(date, current));
                slot.setEndTime(LocalDateTime.of(date, slotEnd));
                slot.setStatus(SlotStatus.FREE);
                slot.setPrice((double) (defaultPrice != null ? defaultPrice : 0));
                slotRepository.save(slot);
                slotCount++;
            }

            current = current.plusMinutes(duration);
        }

        System.out.println("Создано слотов на " + date + ": " + slotCount);
    }

    @Transactional
    public void createSlotsForShift(Long doctorId, SlotCreationRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorSchedule tempSchedule = new DoctorSchedule();
        tempSchedule.setDoctor(doctor);
        tempSchedule.setStartTime(request.getStartTime());
        tempSchedule.setEndTime(request.getEndTime());
        tempSchedule.setSlotDurationMinutes(request.getSlotDuration());

        generateSlotsForDateWithCustomBreak(
                doctorId,
                request.getDate(),
                tempSchedule,
                request.getBreakStart(),
                request.getBreakEnd(),
                request.getPrice()
        );
    }

    @Transactional
    public void generateSlotsForDateWithCustomBreak(Long doctorId, LocalDate date, DoctorSchedule schedule,
                                                    LocalTime breakStart, LocalTime breakEnd, Integer price) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<AppointmentSlot> existingSlots = slotRepository.findByDoctorIdAndStartTimeBetween(
                doctorId, startOfDay, endOfDay
        );

        if (!existingSlots.isEmpty()) {
            slotRepository.deleteAll(existingSlots);
        }

        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();
        int duration = schedule.getSlotDurationMinutes();

        LocalTime current = start;
        int slotCount = 0;

        while (current.isBefore(end)) {
            LocalTime slotEnd = current.plusMinutes(duration);

            boolean isBreak = !(slotEnd.isBefore(breakStart) || current.isAfter(breakEnd));

            if (!isBreak && (slotEnd.isBefore(end) || slotEnd.equals(end))) {
                AppointmentSlot slot = new AppointmentSlot();
                slot.setDoctor(schedule.getDoctor());
                slot.setStartTime(LocalDateTime.of(date, current));
                slot.setEndTime(LocalDateTime.of(date, slotEnd));
                slot.setStatus(SlotStatus.FREE);
                slot.setPrice((double) (price != null ? price : 0));
                slotRepository.save(slot);
                slotCount++;
            }

            current = current.plusMinutes(duration);
        }

        System.out.println("Создано слотов на " + date + ": " + slotCount + " с ценой: " + price);
    }

    @Transactional
    public AppointmentSlot updateSlot(Long slotId, SlotUpdateDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Это не ваш слот");
        }

        if (slot.getStatus() != SlotStatus.FREE) {
            throw new RuntimeException("Нельзя редактировать занятый слот");
        }

        if (dto.getStartTime() != null) {
            slot.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            slot.setEndTime(dto.getEndTime());
        }
        if (dto.getPrice() != null) {
            slot.setPrice(dto.getPrice());
        }

        return slotRepository.save(slot);
    }

    private AppointmentSlotDto toDto(AppointmentSlot slot) {
        AppointmentSlotDto dto = new AppointmentSlotDto();
        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        dto.setPrice(slot.getPrice());
        return dto;
    }
/*
    private AppointmentSlotDto mapToDto(AppointmentSlot slot) {
        AppointmentSlotDto dto = new AppointmentSlotDto();
        dto.setId(slot.getId());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        return dto;
    }*/
}