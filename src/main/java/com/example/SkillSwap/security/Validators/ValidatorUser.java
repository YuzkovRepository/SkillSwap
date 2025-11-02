package com.example.SkillSwap.security.Validators;

import com.example.SkillSwap.dto.security.LoginRequestDTO;
import com.example.SkillSwap.dto.security.RegisterRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ValidatorUser {
    public void validateRegisterRequest(RegisterRequestDTO loginRequestDTO) {
        if (loginRequestDTO.login() == null || loginRequestDTO.login().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть нулевым или пустым");
        }
        if (loginRequestDTO.password() == null || loginRequestDTO.password().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или пустым");
        }
        if (loginRequestDTO.email() == null || loginRequestDTO.email().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или пустым");
        }
        if (loginRequestDTO.phone() == null || loginRequestDTO.phone().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона не может быть нулевым или пустым");
        }
    }

    public void validateLoginRequest(LoginRequestDTO requestDTO) {
        if (requestDTO.loginOrEmail() == null || requestDTO.loginOrEmail().isEmpty()) {
            throw new IllegalArgumentException("Логин или адрес электронной почты не могут быть пустыми");
        }
        if (requestDTO.password() == null || requestDTO.password().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть нулевым или незаполненным");
        }
    }
}
