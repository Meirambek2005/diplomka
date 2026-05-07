package com.example.medicalappointment.dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data

public class AppointmentDto {

    private Long id;

    // Информация о пациенте (нужна врачу)
    private String patientFullName;
    private String patientPhone;

    // Информация о враче (нужна пациенту)
    private Long doctorId;
    private String doctorFullName;
    private String doctorSpecialty;
    private String doctorPhone;


    // Время приёма
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Статус и дополнительные данные
    private String status;          // BOOKED, COMPLETED, CANCELLED
    private String reason;          // причина записи / жалоба (если есть)
    private LocalDateTime completedAt;  // когда приём завершён
    private LocalDateTime createdAt;    // когда запись создана

    // Адрес клиники (если нужно)
    private String clinicAddress;

    // ОБЯЗАТЕЛЬНО ДОБАВЬТЕ ЭТИ ПОЛЯ:
    private String symptoms;      // Жалобы
    private String diagnosis;     // Диагноз
    private String recommendations; // Рекомендации

    private Boolean isOnline;
    private String meetingLink;

    private Double price;



    // ГЕТТЕРЫ

    public Long getId() {
        return id;
    }

    public String getPatientFullName() {
        return patientFullName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }


    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    // СЕТТЕРЫ

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatientFullName(String patientFullName) {
        this.patientFullName = patientFullName;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }


    // ГЕТТЕРЫ И СЕТТЕРЫ ДЛЯ НОВЫХ ПОЛЕЙ
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}
