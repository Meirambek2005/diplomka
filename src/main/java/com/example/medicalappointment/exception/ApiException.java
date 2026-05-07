package com.example.medicalappointment.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
