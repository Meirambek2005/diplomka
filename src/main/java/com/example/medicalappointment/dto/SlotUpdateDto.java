package com.example.medicalappointment.dto;

import java.time.LocalDateTime;

public class SlotUpdateDto {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double price;

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Double getPrice() {
        return price;
    }

    // Исправленный сеттер: теперь принимает Double
    public void setPrice(Double price) {
        this.price = price;
    }
}
