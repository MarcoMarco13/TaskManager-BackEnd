package com.projeto.projeto.controller;

import com.projeto.projeto.domain.User;
import com.projeto.projeto.dto.LoginDTO;
import com.projeto.projeto.dto.RegisterDTO;
import com.projeto.projeto.dto.TokenResponseDTO;
import com.projeto.projeto.repository.UserRepository;
import com.projeto.projeto.security.RateLimiterService;
import com.projeto.projeto.security.RecaptchaService;
import com.projeto.projeto.security.TokenService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RateLimiterService rateLimiterService;
    private final RecaptchaService recaptchaService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService,
                          RateLimiterService rateLimiterService,
                          RecaptchaService recaptchaService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.rateLimiterService = rateLimiterService;
        this.recaptchaService = recaptchaService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data, HttpServletRequest request) {
        if (data == null || data.email() == null || data.password() == null) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios.");
        }

        try {
            String normalizedEmail = data.email().trim().toLowerCase();

            var usernamePassword = new UsernamePasswordAuthenticationToken(normalizedEmail, data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new TokenResponseDTO(token));

        } catch (Exception e) {
            // Mostra a causa real (Ex: "User is locked", "Bad credentials", "User not found")
            System.err.println("ERRO DE AUTENTICAÇÃO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro: " + e.getMessage());
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (data == null || data.email() == null || data.password() == null) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios.");
        }

        String normalizedEmail = data.email().trim().toLowerCase();

        if (this.userRepository.findByEmail(normalizedEmail) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        String name = (data.name() != null && !data.name().isBlank()) ? data.name() : normalizedEmail;
        String encryptedPassword = passwordEncoder.encode(data.password());

        User newUser = new User(normalizedEmail, encryptedPassword, name);
        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}