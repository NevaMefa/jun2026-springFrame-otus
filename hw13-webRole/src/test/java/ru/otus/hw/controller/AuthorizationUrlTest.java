package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.services.BookPermissionService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CustomUserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
@Import(SecurityConfig.class)
class AuthorizationUrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookPermissionService bookPermissionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void userCanReadBooks() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanReadBookById() throws Exception {
        BookDto book = new BookDto();
        book.setId(1L);
        when(bookService.findById(1L)).thenReturn(java.util.Optional.of(book));
        when(bookPermissionService.canReadBook(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateBook() throws Exception {
        String bookJson = """
                {
                    "title": "New Book",
                    "authorId": 1,
                    "genreIds": [1]
                }
                """;
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotUpdateBook() throws Exception {
        String bookJson = """
                {
                    "title": "Updated Book",
                    "authorId": 1,
                    "genreIds": [1]
                }
                """;
        mockMvc.perform(put("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReadBooks() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateBook() throws Exception {
        BookDto book = new BookDto();
        book.setId(10L);
        when(bookService.insert(any(CreateBookRequestDto.class))).thenReturn(book);

        String bookJson = """
                {
                    "title": "New Book",
                    "authorId": 1,
                    "genreIds": [1]
                }
                """;
        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdateBook() throws Exception {
        BookDto book = new BookDto();
        book.setId(1L);
        when(bookService.update(anyLong(), any(UpdateBookRequestDto.class))).thenReturn(book);
        when(bookPermissionService.canEditBook(1L)).thenReturn(true);

        String bookJson = """
                {
                    "title": "Updated Book",
                    "authorId": 1,
                    "genreIds": [1]
                }
                """;
        mockMvc.perform(put("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteBook() throws Exception {
        when(bookPermissionService.canDeleteBook(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isNoContent());
    }
}