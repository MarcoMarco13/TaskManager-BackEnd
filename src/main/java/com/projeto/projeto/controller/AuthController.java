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
        String clientIp = request.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(clientIp);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Muitas tentativas de login. Aguarde 1 minuto e tente novamente.");
        }

        if (!recaptchaService.validateCaptcha(data.captchaToken())) {
            return ResponseEntity.badRequest().body("Validação do reCAPTCHA falhou.");
        }

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO data) {
        if (this.userRepository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.email(), encryptedPassword, data.name());

        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }
}