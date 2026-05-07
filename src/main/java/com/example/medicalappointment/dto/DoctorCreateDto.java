package com.example.medicalappointment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCreateDto {

    @NotBlank(message = "Специальность обязательна")
    @Size(min = 3, max = 100, message = "Специальность от 3 до 100 символов")
    private String specialty;

    @NotNull(message = "Опыт обязателен")
    @Min(value = 0, message = "Опыт не может быть отрицательным")
    @Max(value = 70, message = "Опыт не может быть больше 70 лет")
    private Integer experienceYears;

    // Рейтинг можно задать по умолчанию 0.0, или не требовать при создании
    private Double rating = 0.0;

    // Дополнительно можно добавить: город, клиника, фото и т.д.
    private String city;

    // --- getters / setters ---

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
