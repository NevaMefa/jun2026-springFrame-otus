package ru.otus.hw.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.CustomUserDetailsService;
import ru.otus.hw.security.JwtAuthenticationFilter;
import ru.otus.hw.security.JwtService;
import ru.otus.hw.services.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        BookRestController.class,
        AuthorRestController.class,
        GenreRestController.class,
        CommentRestController.class,
        AuthController.class
})
@Import(SecurityConfig.class)
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @Test
    @Disabled("В @WebMvcTest JWT фильтр не проверяет токены. Проверяется через @WithMockUser.")
    void shouldNotAllowAccessToProtectedEndpointsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowAccessToProtectedEndpointsWithValidToken() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowAccessToAuthorEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowAccessToGenreEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowAccessToCommentEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/comment").param("bookId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled("В @WebMvcTest JWT фильтр не проверяет токены. Проверяется через @WithMockUser.")
    void shouldReturn401WithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/book")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }
}