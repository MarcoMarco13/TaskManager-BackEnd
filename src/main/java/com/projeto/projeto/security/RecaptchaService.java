package com.projeto.projeto.security;

import org.springframework.stereotype.Service;

@Service
public class RecaptchaService {

    public boolean validateCaptcha(String captchaToken) {
        // DESATIVADO TEMPORARIAMENTE PARA TESTES
        return true;
    }
}