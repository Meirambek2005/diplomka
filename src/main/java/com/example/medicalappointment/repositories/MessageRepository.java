package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    List<Message> findBySlotIdOrderByCreatedAtAsc(Long slotId);


}
