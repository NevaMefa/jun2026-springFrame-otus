package ru.otus.hw.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.services.BookService;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldListAllBooks() throws Exception {
        List<BookDto> expectedBooks = createBookDtos();

        when(bookService.findAll()).thenReturn(expectedBooks);

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBooks)));

        verify(bookService).findAll();
    }

    @Test
    void shouldGetBookById() throws Exception {
        BookDto expectedBook = createBookDto(1L, "Book 1");
        when(bookService.findById(1L)).thenReturn(Optional.of(expectedBook));

        mockMvc.perform(get("/api/v1/book/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBook)));

        verify(bookService).findById(1L);
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/v1/book/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById(1L);
    }

    @Test
    void shouldCreateBook() throws Exception {
        CreateBookRequestDto request = new CreateBookRequestDto("New Book", 1L, Set.of(1L, 2L));
        BookDto createdBook = createBookDto(10L, "New Book");

        when(bookService.insert(any(CreateBookRequestDto.class))).thenReturn(createdBook);

        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(new CreatedEntityDto(10L))));

        verify(bookService).insert(any(CreateBookRequestDto.class));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        UpdateBookRequestDto request = new UpdateBookRequestDto("Updated Book", 2L, Set.of(1L, 2L));
        BookDto updatedBook = createBookDto(1L, "Updated Book");

        when(bookService.update(eq(1L), any(UpdateBookRequestDto.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/v1/book/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedBook)));

        verify(bookService).update(eq(1L), any(UpdateBookRequestDto.class));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(bookService.findById(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/book/{id}", 100L))
                .andExpect(status().isNotFound());

        verify(bookService).findById(100L);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        when(bookService.findAll()).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isInternalServerError());

        verify(bookService).findAll();
    }

    @Test
    void shouldReturn400WhenInvalidCreateInput() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto("", null, null);

        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInvalidUpdateInput() throws Exception {
        UpdateBookRequestDto invalidRequest = new UpdateBookRequestDto("", null, null);

        mockMvc.perform(put("/api/v1/book/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    private List<BookDto> createBookDtos() {
        return List.of(
                createBookDto(1L, "Book 1"),
                createBookDto(2L, "Book 2"),
                createBookDto(3L, "Book 3")
        );
    }

    private BookDto createBookDto(long id, String title) {
        BookDto book = new BookDto();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(new AuthorDto(id, "Author " + id));
        book.setGenres(List.of(
                new GenreDto(1L, "Genre 1"),
                new GenreDto(2L, "Genre 2")
        ));
        return book;
    }
}