package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.UserUpdateDto;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь с именем " + username + " не найден"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public User updateProfile(String username, UserUpdateDto dto) {
        log.info("Updating profile for user: {}", username);

        User user = findByUsername(username);

        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            log.info("Updating fullName from '{}' to '{}'", user.getFullName(), dto.getFullName());
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            log.info("Updating email from '{}' to '{}'", user.getEmail(), dto.getEmail());
            // Проверка, что email не занят другим пользователем
            userRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getUsername().equals(username)) {
                    throw new RuntimeException("Email already in use by another user");
                }
            });
            user.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            log.info("Updating phone from '{}' to '{}'", user.getPhone(), dto.getPhone());
            user.setPhone(dto.getPhone());
        }

        User savedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", username);

        return savedUser;
    }

    @Transactional
    public User updateFullProfile(String username, UserUpdateDto dto) {
        User user = findByUsername(username);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        return userRepository.save(user);
    }
}