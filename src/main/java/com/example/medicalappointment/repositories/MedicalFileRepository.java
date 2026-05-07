package com.example.medicalappointment.repositories;

import com.example.medicalappointment.entities.MedicalFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalFileRepository extends JpaRepository<MedicalFile, Long> {

    // Найти все файлы по ID слота (приема)
    List<MedicalFile> findBySlotId(Long slotId);

    // Найти все файлы по ID слота с сортировкой по дате загрузки
    @Query("SELECT m FROM MedicalFile m WHERE m.slot.id = :slotId ORDER BY m.uploadedAt DESC")
    List<MedicalFile> findBySlotIdOrderByUploadedAtDesc(@Param("slotId") Long slotId);

    // Найти все файлы пациента по его ID
    @Query("SELECT m FROM MedicalFile m WHERE m.slot.patient.id = :patientId ORDER BY m.uploadedAt DESC")
    List<MedicalFile> findBySlotPatientId(@Param("patientId") Long patientId);

}








