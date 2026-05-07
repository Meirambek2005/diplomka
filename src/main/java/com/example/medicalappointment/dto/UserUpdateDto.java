package com.example.medicalappointment.dto;
/*
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
*/
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для обновления профиля пользователя.
 * Аннотация @Data от Lombok автоматически создает:
 * - Геттеры (getFullName, getEmail, getPhone)
 * - Сеттеры (setFullName, setEmail, setPhone)
 * - equals, hashCode и toString
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private String fullName;
    private String email;
    private String phone;

    // Если Lombok не подхватывается вашей IDE,
    // добавьте эти методы вручную (раскомментируйте их):

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

}