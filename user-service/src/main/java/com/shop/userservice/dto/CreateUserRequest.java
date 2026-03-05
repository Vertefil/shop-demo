package com.shop.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Username обязателен")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неккоректный email")
    private String email;

    @NotBlank(message = "Password обязателен")
    @Size(min = 6, message = "Password минимум 6 символов")
    private String Password;
}
