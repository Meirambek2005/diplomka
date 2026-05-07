package com.example.medicalappointment.schedulers;

import com.example.medicalappointment.entities.AppointmentSlot;
import com.example.medicalappointment.entities.SlotStatus;
import com.example.medicalappointment.repositories.AppointmentSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component

public class AppointmentStatusScheduler {

    private final AppointmentSlotRepository slotRepository;

    // Явно добавляем конструктор — это снимет ошибку
    public AppointmentStatusScheduler(AppointmentSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Scheduled(fixedRate = 60000) // 1 минут
    public void completePastAppointments() {
        LocalDateTime now = LocalDateTime.now();


        List<AppointmentSlot> pastAppointments = slotRepository.findByStatusAndStartTimeBefore(
                SlotStatus.BOOKED,
                now
        );

        if (!pastAppointments.isEmpty()) {
            pastAppointments.forEach(slot -> {
                slot.setStatus(SlotStatus.COMPLETED);
                slot.setCompletedAt(now);
                System.out.println("Завершаем слот id=" + slot.getId() + ", completedAt=" + now);
                slotRepository.save(slot);
            });

            System.out.println("Автоматически завершено " + pastAppointments.size() + " записей");
        }
    }
}