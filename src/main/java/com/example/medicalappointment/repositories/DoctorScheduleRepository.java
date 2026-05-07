package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.Doctor;
import com.example.medicalappointment.entities.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    Optional<DoctorSchedule> findByDoctorId(Long doctorId);
}
