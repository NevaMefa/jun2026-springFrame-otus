package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GenreRestControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowUserToViewGenres() throws Exception {
        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToCreateGenre() throws Exception {
        mockMvc.perform(post("/api/v1/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Genre\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToDeleteGenre() throws Exception {
        mockMvc.perform(delete("/api/v1/genre/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToCreateGenre() throws Exception {
        when(genreService.insert(any())).thenReturn(new GenreDto());

        mockMvc.perform(post("/api/v1/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Genre\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToDeleteGenre() throws Exception {
        mockMvc.perform(delete("/api/v1/genre/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isForbidden());
    }
}