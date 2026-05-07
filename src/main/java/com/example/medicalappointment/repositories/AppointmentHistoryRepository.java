package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.AppointmentHistory;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, Long> {
    List<AppointmentHistory> findAllByPatientIdOrderByStartTimeDesc(Long patientId);

}
