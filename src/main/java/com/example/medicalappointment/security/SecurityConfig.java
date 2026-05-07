package com.example.medicalappointment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // ==================== 1. ПУБЛИЧНЫЕ ЭНДПОИНТЫ ====================
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors/*").permitAll()
                        .requestMatchers("/api/doctors/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors/*/slots").permitAll()

                        // ==================== 2. ДОКТОР (ВРАЧ) ====================
                        // Статистика врача
                        .requestMatchers("/api/doctors/me/stats").hasRole("DOCTOR")
                        .requestMatchers("/api/doctors/me/stats/chart").hasRole("DOCTOR")
                        .requestMatchers("/api/doctors/me/stats/hourly").hasRole("DOCTOR")

                        // Слоты врача
                        .requestMatchers("/api/doctors/me/slots").hasRole("DOCTOR")
                        .requestMatchers("/api/doctors/me/slots/*").hasRole("DOCTOR")
                        .requestMatchers("/api/doctors/me/slots/**").hasRole("DOCTOR")

                        // Приемы врача
                        .requestMatchers("/api/appointments/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/appointments/*/complete").hasRole("DOCTOR")
                        .requestMatchers("/api/doctors/me/appointments").hasRole("DOCTOR")

                        // Шаблоны врача
                        .requestMatchers("/api/templates/**").hasRole("DOCTOR")

                        // Просмотр данных пациентов (врач)
                        .requestMatchers(HttpMethod.GET, "/api/patients/*/profile-data").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/patients/*/medical-data").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/patients/*/documents").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/patients/*/medical-card").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/patients/*/medical-card").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/patients/*/medical-card").hasRole("DOCTOR")

                        // Медицинские файлы (врач может просматривать)
                        .requestMatchers(HttpMethod.GET, "/api/medical-files/**").hasAnyRole("PATIENT", "DOCTOR")

                        // ==================== 3. ПАЦИЕНТ ====================
                        // Профиль пациента
                        .requestMatchers("/api/patients/profile").hasRole("PATIENT")
                        .requestMatchers("/api/patients/my/**").hasRole("PATIENT")

                        // Запись к врачу
                        .requestMatchers(HttpMethod.POST, "/api/appointments/book/**").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/appointments/book").hasRole("PATIENT")
                        .requestMatchers("/api/appointments/*/cancel").hasRole("PATIENT")

                        // Медицинские файлы (пациент загружает и удаляет)
                        .requestMatchers(HttpMethod.POST, "/api/medical-files/**").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/medical-files/**").hasRole("PATIENT")

                        // Отзывы (пациент оставляет)
                        .requestMatchers(HttpMethod.POST, "/api/reviews/doctor/*").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/reviews/doctor/**").hasRole("PATIENT")

                        // ==================== 4. ОБЩИЙ ДОСТУП ДЛЯ АВТОРИЗОВАННЫХ ====================
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/chat/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/my/history").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/*/online").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/doctor/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/doctor/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/appointments/**").hasAnyRole("PATIENT", "DOCTOR")

                        // ==================== 5. ВСЕ ОСТАЛЬНЫЕ ЗАПРОСЫ ====================
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}