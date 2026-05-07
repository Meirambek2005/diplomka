package com.example.medicalappointment.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDto {

    private Long id;
    private String username;
    private String email;
    private String specialty;
    private Integer experienceYears;
    private Double rating;
    private String fullName; // ПОЛЕ ДОБАВЛЕНО
    private String city;

    // Явные методы для гарантии компиляции
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFullName() { return fullName; }

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setUsername(String username) { this.username = username; }
    public String getUsername() { return username; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getSpecialty() { return specialty; }

    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public Integer getExperienceYears() { return experienceYears; }

    public void setRating(Double rating) { this.rating = rating; }
    public Double getRating() { return rating; }

    @Override
    public String toString() {
        return "DoctorResponseDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", specialty='" + specialty + '\'' +
                '}';
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}