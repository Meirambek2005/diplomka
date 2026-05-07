package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.MessageDto;
import com.example.medicalappointment.entities.Message;
import com.example.medicalappointment.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
@RestController
@RequestMapping("/api/chat")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{slotId}")
    public ResponseEntity<MessageDto> send(
            @PathVariable Long slotId,
            @RequestBody String content,
            Authentication auth) {

        return ResponseEntity.ok(
                messageService.send(slotId, auth.getName(), content)
        );
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<List<MessageDto>> getChat(@PathVariable Long slotId) {
        return ResponseEntity.ok(messageService.getChat(slotId));
    }

    @GetMapping("/slot/{slotId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long slotId) {
        return ResponseEntity.ok(messageService.getMessages(slotId));
    }
}