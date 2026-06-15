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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

@WebMvcTest(AuthorRestController.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldListAllAuthors() throws Exception {
        List<AuthorDto> expectedAuthors = List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2"),
                new AuthorDto(3L, "Author_3")
        );

        when(authorService.findAll()).thenReturn(expectedAuthors);

        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedAuthors)));

        verify(authorService).findAll();
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        AuthorDto expectedAuthor = new AuthorDto(1L, "Author_1");
        when(authorService.findById(1L)).thenReturn(Optional.of(expectedAuthor));

        mockMvc.perform(get("/api/v1/author/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedAuthor)));

        verify(authorService).findById(1L);
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/v1/author/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById(1L);
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        CreateAuthorRequestDto request = new CreateAuthorRequestDto("New Author");
        AuthorDto createdAuthor = new AuthorDto(10L, "New Author");

        when(authorService.insert(any(CreateAuthorRequestDto.class))).thenReturn(createdAuthor);

        mockMvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(new CreatedEntityDto(10L))));

        verify(authorService).insert(any(CreateAuthorRequestDto.class));
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        UpdateAuthorRequestDto request = new UpdateAuthorRequestDto("Updated Author");
        AuthorDto updatedAuthor = new AuthorDto(1L, "Updated Author");

        when(authorService.update(eq(1L), any(UpdateAuthorRequestDto.class))).thenReturn(updatedAuthor);

        mockMvc.perform(put("/api/v1/author/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedAuthor)));

        verify(authorService).update(eq(1L), any(UpdateAuthorRequestDto.class));
    }

    @Test
    void shouldReturn404WhenAuthorNotFound() throws Exception {
        when(authorService.findById(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/author/{id}", 100L))
                .andExpect(status().isNotFound());

        verify(authorService).findById(100L);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        when(authorService.findAll()).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isInternalServerError());

        verify(authorService).findAll();
    }

    @Test
    void shouldReturn400WhenInvalidCreateInput() throws Exception {
        CreateAuthorRequestDto invalidRequest = new CreateAuthorRequestDto("");

        mockMvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInvalidUpdateInput() throws Exception {
        UpdateAuthorRequestDto invalidRequest = new UpdateAuthorRequestDto("");

        mockMvc.perform(put("/api/v1/author/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}