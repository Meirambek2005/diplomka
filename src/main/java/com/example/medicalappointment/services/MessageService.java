package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.MessageDto;
import com.example.medicalappointment.entities.*;
import com.example.medicalappointment.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentSlotRepository slotRepository;


    public MessageService(MessageRepository messageRepository,
                          AppointmentRepository appointmentRepository,
                          UserRepository userRepository,
                          AppointmentSlotRepository slotRepository) {

        this.messageRepository = messageRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
    }

    // 📌 отправить сообщение
    public MessageDto send(Long slotId, String username, String content) {

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        User user = userRepository.findByUsername(username).orElseThrow();

        Message message = new Message();
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setSlot(slot);

        // роль
        if (slot.getDoctor().getUserId().equals(user.getId())) {
            message.setSenderRole("DOCTOR");
        } else {
            message.setSenderRole("PATIENT");
        }

        return mapToDto(messageRepository.save(message));
    }

    // 📌 получить чат
    public List<MessageDto> getChat(Long slotId) {

        return messageRepository
                .findBySlotIdOrderByCreatedAtAsc(slotId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<MessageDto> getMessages(Long slotId) {

        return messageRepository
                .findBySlotIdOrderByCreatedAtAsc(slotId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private MessageDto mapToDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setContent(m.getContent());
        dto.setSenderRole(m.getSenderRole());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }


}