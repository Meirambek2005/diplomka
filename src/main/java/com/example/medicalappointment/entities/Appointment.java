package com.example.medicalappointment.entities;

import jakarta.persistence.*;
import lombok.*;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private AppointmentSlot slot;

    @Column(nullable = false)
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    /*@Column(nullable = false)
    private String status; */ // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String symptoms;      // Жалобы

    @Column(columnDefinition = "TEXT")
    private String diagnosis;     // Диагноз

    @Column(columnDefinition = "TEXT")
    private String recommendations; // Рекомендации

    private LocalDateTime completedAt; // Время фактического завершения

    //private Integer price;



    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public LocalDateTime getTime() { return time; }
   // public String getStatus() { return status; }
    public AppointmentSlot getSlot() {
        return slot;
    }

    public void setSlot(AppointmentSlot slot) {
        this.slot = slot;
    }

    //public Integer getPrice() { return price; }
    //public void setPrice(Integer price) { this.price = price; }


}
