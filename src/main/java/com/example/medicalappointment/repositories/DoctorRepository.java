package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Поиск по части специальности (работает, потому что поле specialty есть)
    List<Doctor> findBySpecialtyContainingIgnoreCase(String specialty);

    // Метод по userId — работает, потому что поле userId есть
    Optional<Doctor> findByUserId(Long userId);

    List<Doctor> findByClinicAddressContainingIgnoreCase(String city);

    List<Doctor> findByRatingGreaterThanEqual(Double rating);

    // 🔥 комбинированный поиск
    List<Doctor> findBySpecialtyContainingIgnoreCaseAndCityContainingIgnoreCaseAndRatingGreaterThanEqual(
            String specialty,
            String city,
            Double rating
    );
    @Query("""
    SELECT d FROM Doctor d
    WHERE LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))
    AND LOWER(d.city) LIKE LOWER(CONCAT('%', :city, '%'))
    AND d.rating >= :rating
    """)
    List<Doctor> searchDoctors(String specialty, String city, Double rating);

}