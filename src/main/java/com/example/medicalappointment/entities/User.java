package com.example.medicalappointment.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;


    @Enumerated(EnumType.STRING)   // хранится как строка в БД ("PATIENT", "DOCTOR")
    @Column(nullable = false)
    private Role role;

    @Column(name = "full_name", length = 150)
    private String fullName;

    private String phone;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String iin;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String gender;

    private String address;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    //  ДОБАВЛЯЕМ ПОЛЕ createdAt
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //  ДОБАВЛЯЕМ ПОЛЕ updatedAt (опционально)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //  ДОБАВЛЯЕМ МЕТОД ДЛЯ АВТОМАТИЧЕСКОЙ УСТАНОВКИ ДАТЫ
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Явные геттеры — добавляем, чтобы точно работало
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getPhone() { return phone;}

    //  ГЕТТЕР ДЛЯ createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    //  ГЕТТЕР ДЛЯ updatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Если нужно — сеттеры тоже
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) { this.phone = phone; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }


    public String getIin() { return iin; }
    public void setIin(String iin) { this.iin = iin; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = LocalDate.parse(birthDate); }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }



}