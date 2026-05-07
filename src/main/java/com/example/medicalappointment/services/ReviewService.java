package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.ReviewDto;
import com.example.medicalappointment.entities.Doctor;
import com.example.medicalappointment.entities.Review;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.repositories.DoctorRepository;
import com.example.medicalappointment.repositories.ReviewRepository;
import com.example.medicalappointment.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         DoctorRepository doctorRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создание нового отзыва и автоматическое обновление среднего рейтинга врача
     */
    @Transactional
    public ReviewDto createReview(Long doctorId, String patientUsername, Integer rating, String comment) {
        // 1. Находим врача
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Врач не найден с ID: " + doctorId));

        // 2. Находим пациента (текущего пользователя)
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("Пациент не найден: " + patientUsername));

        // 3. Создаем объект отзыва (используем конструктор вместо builder)
        Review review = new Review(doctor, patient, rating, comment);

        // 4. Сохраняем отзыв
        reviewRepository.save(review);

        // 5. ПЕРЕСЧЕТ РЕЙТИНГА ВРАЧА
        updateDoctorRating(doctor);

        return mapToDto(review);
    }

    /**
     * Вспомогательный метод для пересчета среднего рейтинга
     */
    private void updateDoctorRating(Doctor doctor) {
        List<Review> reviews = reviewRepository.findAllByDoctorId(doctor.getId());

        // Вычисляем среднее арифметическое всех оценок
        double average = reviews.stream()
                .mapToInt(r -> r.getRating()) // Используем лямбду, чтобы не зависеть от Lombok
                .average()
                .orElse(0.0);

        // Сохраняем новый рейтинг в профиль врача
        doctor.setRating(average);
        doctorRepository.save(doctor);
    }


     // Получение всех отзывов конкретного врача

    @Transactional(readOnly = true)
    public List<ReviewDto> getDoctorReviews(Long doctorId) {
        return reviewRepository.findAllByDoctorId(doctorId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


     // Маппинг сущности в DTO вручную (гарантия отсутствия ошибок компиляции)

    private ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setDoctorId(review.getDoctor().getId());

        // Берем имя пациента из связанной сущности User
        if (review.getPatient() != null) {
            dto.setPatientFullName(review.getPatient().getFullName());
        }

        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}