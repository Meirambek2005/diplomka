package com.example.medicalappointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SlotCreationRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDuration;
    private LocalTime breakStart;
    private LocalTime breakEnd;

    // ✅ НОВОЕ ПОЛЕ: Цена
    private Integer price;

    // Геттеры и сеттеры
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getSlotDuration() { return slotDuration; }
    public void setSlotDuration(Integer slotDuration) { this.slotDuration = slotDuration; }

    public LocalTime getBreakStart() { return breakStart; }
    public void setBreakStart(LocalTime breakStart) { this.breakStart = breakStart; }

    public LocalTime getBreakEnd() { return breakEnd; }
    public void setBreakEnd(LocalTime breakEnd) { this.breakEnd = breakEnd; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}