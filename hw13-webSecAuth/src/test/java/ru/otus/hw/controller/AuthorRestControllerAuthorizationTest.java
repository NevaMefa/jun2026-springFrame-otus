package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorRestControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowUserToViewAuthors() throws Exception {
        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToCreateAuthor() throws Exception {
        mockMvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New Author\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/v1/author/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToCreateAuthor() throws Exception {
        when(authorService.insert(any())).thenReturn(new AuthorDto());

        mockMvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New Author\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/v1/author/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isForbidden());
    }
}