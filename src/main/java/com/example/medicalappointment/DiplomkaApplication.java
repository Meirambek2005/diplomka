package com.example.medicalappointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.example.medicalappointment.repositories")  // ← обязательно для репозиториев
public class DiplomkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiplomkaApplication.class, args);
    }
}
