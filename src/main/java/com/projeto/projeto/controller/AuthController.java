package com.projeto.projeto.controller;

import com.projeto.projeto.domain.User;
import com.projeto.projeto.dto.LoginDTO;
import com.projeto.projeto.dto.RegisterDTO;
import com.projeto.projeto.dto.TokenResponseDTO;
import com.projeto.projeto.repository.UserRepository;
import com.projeto.projeto.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO data) {
        if (data == null || data.email() == null || data.password() == null) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios.");
        }

        try {
            String normalizedEmail = data.email().trim().toLowerCase();
            var usernamePassword = new UsernamePasswordAuthenticationToken(normalizedEmail, data.password());

            // O Spring vai no banco, busca o usuário, compara a senha e autentica sozinho aqui:
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new TokenResponseDTO(token));

        } catch (Exception e) {
            // Se a senha for errada ou o usuário não existir, cai direto aqui.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (data == null || data.email() == null || data.password() == null) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios.");
        }

        String normalizedEmail = data.email().trim().toLowerCase();

        // Atualizado para usar o Optional:
        if (this.userRepository.findByEmail(normalizedEmail).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        String name = (data.name() != null && !data.name().isBlank()) ? data.name() : normalizedEmail;
        String encryptedPassword = passwordEncoder.encode(data.password());

        User newUser = new User(normalizedEmail, encryptedPassword, name);
        this.userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}