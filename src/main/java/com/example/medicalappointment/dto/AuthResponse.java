package com.example.medicalappointment.dto;

import com.example.medicalappointment.entities.Role;

public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role;

    // Пустой конструктор
    public AuthResponse() {}

    // Конструктор со всеми полями
    public AuthResponse(String token, String username, String email, String fullName, String phone, Role role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    // Геттеры и сеттеры
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // Статический builder класс
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private Role role;

        public Builder token(String token) { this.token = token; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder role(Role role) { this.role = role; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, username, email, fullName, phone, role);
        }
    }
}