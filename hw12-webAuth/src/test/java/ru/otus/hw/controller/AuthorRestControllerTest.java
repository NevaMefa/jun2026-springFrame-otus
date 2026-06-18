package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.CustomUserDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorRestController.class)
@Import(SecurityConfig.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldRedirectToLoginWhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void shouldReturnOkWhenAccessingWithAuth() throws Exception {
        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk());
    }
}