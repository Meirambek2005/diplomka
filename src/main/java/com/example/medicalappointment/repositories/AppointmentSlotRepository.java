package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.AppointmentSlot;
import com.example.medicalappointment.entities.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    // КРИТИЧЕСКИЙ ФИКС: Метод, который искал AppointmentService
    List<AppointmentSlot> findAllByDoctorId(Long doctorId);

    // Метод для генерации расписания без дубликатов (ошибка 409)
    boolean existsByDoctorIdAndStartTime(Long doctorId, LocalDateTime startTime);

    List<AppointmentSlot> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
    Optional<AppointmentSlot> findByIdAndStatus(Long id, SlotStatus status);
    List<AppointmentSlot> findByPatientIdAndStatus(Long patientId, SlotStatus status);


    @Query("SELECT s FROM AppointmentSlot s WHERE s.patient.id = :patientId ORDER BY s.startTime DESC")
    List<AppointmentSlot> findAllByPatientId(@Param("patientId") Long patientId);

    Page<AppointmentSlot> findByPatientId(Long patientId, Pageable pageable);
    Page<AppointmentSlot> findByPatientIdAndStatus(Long patientId, SlotStatus status, Pageable pageable);
    List<AppointmentSlot> findByStatusAndStartTimeBefore(SlotStatus slotStatus, LocalDateTime now);

    //  Получить все слоты врача
    List<AppointmentSlot> findByDoctorId(Long doctorId);

    //  Удалить ВСЕ слоты врача (для фикса дублей)
    void deleteByDoctorId(Long doctorId);

    //  Проверка пересечений
    boolean existsByDoctorIdAndStartTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    //  Получить только свободные слоты
    List<AppointmentSlot> findByDoctorIdAndStatus(Long doctorId, SlotStatus status);

    List<AppointmentSlot> findByDoctorIdAndStartTimeBetweenOrderByStartTimeAsc(Long doctorId, LocalDateTime start, LocalDateTime end);
    // ✅ НОВЫЙ МЕТОД для поиска ВСЕХ слотов пациента
    List<AppointmentSlot> findByPatientId(Long patientId);




}