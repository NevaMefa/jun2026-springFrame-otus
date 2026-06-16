package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookRestControllerAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldAllowUserToViewBooks() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToCreateBook() throws Exception {
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Book\",\"authorId\":1,\"genreIds\":[1]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldNotAllowUserToDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToCreateBook() throws Exception {
        when(bookService.insert(any())).thenReturn(new BookDto());

        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Book\",\"authorId\":1,\"genreIds\":[1]}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isForbidden());
    }
}