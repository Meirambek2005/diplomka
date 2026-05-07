package com.example.medicalappointment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Appointment API")
                        .version("1.0")
                        .description("Система онлайн-записи к врачам (дипломная работа Ка_барам_06)"))
                // Добавляем схему JWT Bearer
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Введите JWT-токен в формате: Bearer <токен>")))
                // Применяем security глобально (ко всем защищённым эндпоинтам)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
