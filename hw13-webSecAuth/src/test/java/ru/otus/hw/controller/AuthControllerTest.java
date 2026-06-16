package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthRequestDto;
import ru.otus.hw.dto.RegisterRequestDto;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.CustomUserDetailsService;
import ru.otus.hw.security.JwtService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        AuthRequestDto request = new AuthRequestDto("user", "password");
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user")
                .password("password")
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("test-token");

        User user = new User();
        user.setUsername("user");
        user.setRole("USER");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("newuser", "password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encoded");
        savedUser.setRole("USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("newuser")
                .password("encoded")
                .roles("USER")
                .build();
        when(userDetailsService.loadUserByUsername("newuser")).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("test-token");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldReturnConflictWhenUserExists() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("existinguser", "password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterInvalidInput() throws Exception {
        RegisterRequestDto invalidRequest = new RegisterRequestDto("", "");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLoginInvalidInput() throws Exception {
        AuthRequestDto invalidRequest = new AuthRequestDto("", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}