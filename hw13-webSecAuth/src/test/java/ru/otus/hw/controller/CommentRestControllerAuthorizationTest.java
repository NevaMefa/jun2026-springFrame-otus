package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentRestControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowUserToViewComments() throws Exception {
        mockMvc.perform(get("/api/v1/comment").param("bookId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToCreateComment() throws Exception {
        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"New comment\",\"bookId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comment/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToCreateComment() throws Exception {
        when(commentService.insert(any())).thenReturn(new CommentDto());

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"New comment\",\"bookId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comment/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/comment").param("bookId", "1"))
                .andExpect(status().isForbidden());
    }
}