package com.example.SkillSwap.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "The username cannot be empty")
        @Size(min = 8, max = 20, message = "Длина логина должна составлять от 8 до 20 символов")
        String login,

        @NotBlank(message = "The password cannot be empty")
        @Size(min = 8, max = 20, message = "Пароль должен содержать от 8 до 20 символов")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no spaces")
        String password,

        @NotBlank(message = "The email address cannot be empty.")
        @Email(message = "The email address must be valid")
        String email,

        @Size(min = 12, max = 12, message = "the number must be in the format +7 999 999 99 99 no spaces for Russian numbers and up to 15 characters for foreign numbers")
        @NotBlank(message = "The phone number cannot be empty")
        String phone,

        @NotBlank(message = "the \"Name\" field cannot be empty.")
        String firstName,

        @NotBlank(message = "The \"Patronymic\" field cannot be empty.")
        String lastName,

        @NotBlank(message = "the \"Family\" field cannot be empty.")
        String surname
) {}