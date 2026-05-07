package com.example.medicalappointment.services;

import com.example.medicalappointment.dto.MedicalFileDto;
import com.example.medicalappointment.entities.AppointmentSlot;
import com.example.medicalappointment.entities.MedicalFile;
import com.example.medicalappointment.entities.User;
import com.example.medicalappointment.exception.ApiException;
import com.example.medicalappointment.repositories.AppointmentSlotRepository;
import com.example.medicalappointment.repositories.MedicalFileRepository;
import com.example.medicalappointment.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalFileService {

    private final MedicalFileRepository fileRepository;
    private final AppointmentSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final String uploadDir;

    public MedicalFileService(MedicalFileRepository fileRepository,
                              AppointmentSlotRepository slotRepository,
                              UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;

        // Используем временную директорию
        String tempDir = System.getProperty("java.io.tmpdir") + "/medical-files";
        File directory = new File(tempDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        this.uploadDir = tempDir;
        System.out.println("Upload directory initialized: " + uploadDir);
    }

    @Transactional
    public MedicalFileDto addFile(Long slotId, String username, MultipartFile file, String type) {
        System.out.println("=== UPLOAD FILE ===");
        System.out.println("Slot ID: " + slotId);
        System.out.println("Username: " + username);
        System.out.println("File name: " + file.getOriginalFilename());
        System.out.println("File size: " + file.getSize() + " bytes");

        User patient = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Пользователь не найден"));

        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ApiException("Слот не найден"));

        if (slot.getPatient() == null || !slot.getPatient().getId().equals(patient.getId())) {
            throw new ApiException("У вас нет доступа к этому слоту");
        }

        try {
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
            String filePath = uploadDir + File.separator + fileName;

            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            System.out.println("File saved to: " + filePath);

            MedicalFile medicalFile = new MedicalFile();
            medicalFile.setFileUrl("/api/uploads/" + fileName);
            medicalFile.setType(type);
            medicalFile.setSlot(slot);
            medicalFile.setFileName(fileName);
            medicalFile.setOriginalFileName(originalFileName);
            medicalFile.setFileSize(file.getSize());
            medicalFile.setContentType(file.getContentType());
            medicalFile.setUploadedAt(LocalDateTime.now());

            return mapToDto(fileRepository.save(medicalFile));
        } catch (IOException e) {
            throw new ApiException("Ошибка сохранения файла: " + e.getMessage());
        }
    }

    public List<MedicalFileDto> getFilesByAppointment(Long slotId) {
        return fileRepository.findBySlotId(slotId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFile(Long fileId, String username) {
        MedicalFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ApiException("Файл не найден"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Пользователь не найден"));

        if (file.getSlot() == null || file.getSlot().getPatient() == null) {
            throw new ApiException("Невозможно определить владельца файла");
        }

        if (!file.getSlot().getPatient().getId().equals(currentUser.getId())) {
            throw new ApiException("У вас нет прав на удаление этого файла");
        }

        // Удаляем файл с диска
        try {
            String filePath = uploadDir + File.separator + file.getFileName();
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Не удалось удалить файл: " + e.getMessage());
        }

        fileRepository.deleteById(fileId);
    }

    public List<MedicalFileDto> getFilesByPatientId(Long patientId, String doctorUsername) {
        // Проверяем, что пользователь - врач
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!"DOCTOR".equals(doctor.getRole())) {
            throw new RuntimeException("Только врачи могут просматривать файлы пациентов");
        }

        // Проверяем, что пациент существует
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));

        List<MedicalFile> files = fileRepository.findBySlotPatientId(patientId);
        return files.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MedicalFileDto mapToDto(MedicalFile file) {
        MedicalFileDto dto = new MedicalFileDto();
        dto.setId(file.getId());
        dto.setFileUrl(file.getFileUrl());
        dto.setType(file.getType());
        dto.setFileName(file.getFileName());
        dto.setOriginalFileName(file.getOriginalFileName());
        dto.setFileSize(file.getFileSize());
        dto.setContentType(file.getContentType());
        if (file.getUploadedAt() != null) {
            dto.setUploadedAt(file.getUploadedAt());
        }
        if (file.getSlot() != null && file.getSlot().getDoctor() != null) {
            dto.setDoctorName(file.getSlot().getDoctor().getFullName());
        }
        return dto;
    }
}