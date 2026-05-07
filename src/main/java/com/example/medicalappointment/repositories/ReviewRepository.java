package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByDoctorId(Long doctorId);
    @Query("""
    SELECT AVG(r.rating)
    FROM Review r
    WHERE r.doctor.id = :doctorId
    """)
    Double getAverageRating(Long doctorId);
}
