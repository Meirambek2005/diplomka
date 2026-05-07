package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.Appointment;
import com.example.medicalappointment.entities.AppointmentStatus;
import com.example.medicalappointment.entities.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDoctorIdAndPatientIsNull(Long doctorId);

    Optional<Appointment> findBySlotId(Long slotId);
    @Query("""
    SELECT a FROM Appointment a
    WHERE a.status = 'BOOKED'
    AND a.slot.startTime BETWEEN :now AND :after
    """)
    List<Appointment> findUpcomingAppointments(
            LocalDateTime now,
            LocalDateTime after
    );
    List<Appointment> findAllByDoctorId(Long doctorId);

    // количество завершённых приёмов
    Long countByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    // уникальные пациенты
    @Query("""
    SELECT COUNT(DISTINCT a.patient.id)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    """)
    Long countUniquePatients(Long doctorId);

    @Query("""
    SELECT DATE(a.time), COUNT(a)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND a.status = 'COMPLETED'
    GROUP BY DATE(a.time)
    ORDER BY DATE(a.time)
    """)
    List<Object[]> getAppointmentsStats(Long doctorId);

    @Query("""
    SELECT HOUR(a.time), COUNT(a)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND a.status = 'COMPLETED'
    GROUP BY HOUR(a.time)
    ORDER BY HOUR(a.time)
    """)
    List<Object[]> getHourlyStats(Long doctorId);

    

}