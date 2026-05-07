package com.example.medicalappointment.dto;

import java.time.LocalDateTime;

public class ReviewDto {
    private Long id;
    private Long doctorId;
    private String patientFullName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // --- КОНСТРУКТОРЫ ---
    public ReviewDto() {}

    public ReviewDto(Long id, Long doctorId, String patientFullName, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientFullName = patientFullName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // --- ЯВНЫЕ ГЕТТЕРЫ И СЕТТЕРЫ (Чтобы ReviewService перестал ругаться) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getPatientFullName() { return patientFullName; }
    public void setPatientFullName(String patientFullName) { this.patientFullName = patientFullName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}