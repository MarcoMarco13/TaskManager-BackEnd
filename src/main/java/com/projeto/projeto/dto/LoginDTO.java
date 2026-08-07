package com.projeto.projeto.dto;

public record LoginDTO(
        String email,
        String password,
        String captchaToken
) {}