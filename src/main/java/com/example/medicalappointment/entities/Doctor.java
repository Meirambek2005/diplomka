package com.example.medicalappointment.entities;

import com.example.medicalappointment.dto.UserUpdateDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Просто ID пользователя (foreign key)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    private String specialty;

    private String fullName;

    private Integer experienceYears;

    private Double rating = 0.0;

    @Column(name = "clinic_address", length = 255)
    private String clinicAddress;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "phone")
    private String phone;

    private String city;

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getClinicAddress() { return clinicAddress; }
    public void setClinicAddress(String clinicAddress) { this.clinicAddress = clinicAddress; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    //  ВОТ ЭТО НОВОЕ
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}