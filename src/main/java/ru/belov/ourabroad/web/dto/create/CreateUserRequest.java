package ru.belov.ourabroad.web.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class CreateUserRequest {

    @Email(message = "Email имеет неверный формат")
    @jakarta.validation.constraints.NotBlank(message = "Email обязателен")
    private String email;

    @Pattern(
            regexp = "^\\+\\d{10,15}$",
            message = "Телефон должен быть в формате +79123456789"
    )
    private String phone;

    @jakarta.validation.constraints.NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 16, message = "Пароль должен быть ровно 16 символов")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{16}$",
            message = "Пароль должен содержать минимум 1 заглавную букву и 1 цифру"
    )
    private String password;
}