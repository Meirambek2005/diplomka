package com.example.medicalappointment.services;

import com.example.medicalappointment.entities.Appointment;
import com.example.medicalappointment.repositories.AppointmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final AppointmentRepository appointmentRepository;

    public NotificationService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // 🔔 Проверка каждые 5 минут
    @Scheduled(fixedRate = 300000)
    public void sendReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after = now.plusHours(1);

        List<Appointment> appointments =
                appointmentRepository.findUpcomingAppointments(now, after);

        for (Appointment appointment : appointments) {

            String message = "Напоминание: у вас запись через 1 час. Врач: "
                    + appointment.getDoctor().getFullName()
                    + ", время: " + appointment.getSlot().getStartTime();

            // 📌 просто вывод в лог
            System.out.println(message);
        }
    }
}
