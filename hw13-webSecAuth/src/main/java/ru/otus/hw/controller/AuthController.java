package ru.otus.hw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthRequestDto;
import ru.otus.hw.dto.AuthResponseDto;
import ru.otus.hw.dto.RegisterRequestDto;
import ru.otus.hw.dto.RestApiErrorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.JwtService;
import ru.otus.hw.security.CustomUserDetailsService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(new AuthResponseDto(token, user.getUsername(), user.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new RestApiErrorDto("Conflict", "Username already exists"));
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDto(token, user.getUsername(), user.getRole()));
    }
}