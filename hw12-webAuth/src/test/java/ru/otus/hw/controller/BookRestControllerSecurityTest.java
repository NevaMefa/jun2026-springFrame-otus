package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CustomUserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
@Import(SecurityConfig.class)
class BookRestControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldReturnUnauthorizedWhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().is3xxRedirection()); // 302
    }

    @Test
    @WithMockUser
    void shouldReturnOkWhenAccessingWithUserAuth() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWhenAccessingWithAdminAuth() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingBookByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/book/1"))
                .andExpect(status().is3xxRedirection()); // 302
    }

    @Test
    @WithMockUser
    void shouldReturnOkWhenAccessingBookByIdWithAuth() throws Exception {
        BookDto book = createBookDto(1L, "Test Book");
        when(bookService.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldReturnBookListWhenAuthenticated() throws Exception {
        List<BookDto> books = List.of(
                createBookDto(1L, "Book 1"),
                createBookDto(2L, "Book 2")
        );
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk());
    }

    private BookDto createBookDto(long id, String title) {
        BookDto book = new BookDto();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(new AuthorDto(1L, "Author"));
        book.setGenres(List.of(new GenreDto(1L, "Genre")));
        return book;
    }
}