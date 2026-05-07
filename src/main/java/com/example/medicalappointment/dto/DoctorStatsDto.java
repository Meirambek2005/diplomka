package com.example.medicalappointment.dto;

public class DoctorStatsDto {

    private Long totalPatients;
    private Long completedAppointments;
    private Double averageRating;

    // --- getters / setters ---

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(Long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
