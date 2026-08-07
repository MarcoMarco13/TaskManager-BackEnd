package com.projeto.projeto.dto;

public record RegisterDTO(
        String email,
        String password,
        String name,
        String captchaToken
) {}