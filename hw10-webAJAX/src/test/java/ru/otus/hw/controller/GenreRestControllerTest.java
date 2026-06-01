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

import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.CreatedEntityDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;
import ru.otus.hw.services.GenreService;

@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldListAllGenres() throws Exception {
        List<GenreDto> expectedGenres = List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2"),
                new GenreDto(3L, "Genre_3")
        );

        when(genreService.findAll()).thenReturn(expectedGenres);

        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedGenres)));

        verify(genreService).findAll();
    }

    @Test
    void shouldGetGenreById() throws Exception {
        GenreDto expectedGenre = new GenreDto(1L, "Genre_1");
        when(genreService.findById(1L)).thenReturn(Optional.of(expectedGenre));

        mockMvc.perform(get("/api/v1/genre/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedGenre)));

        verify(genreService).findById(1L);
    }

    @Test
    void shouldDeleteGenre() throws Exception {
        mockMvc.perform(delete("/api/v1/genre/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(genreService).deleteById(1L);
    }

    @Test
    void shouldCreateGenre() throws Exception {
        CreateGenreRequestDto request = new CreateGenreRequestDto("New Genre");
        GenreDto createdGenre = new GenreDto(10L, "New Genre");

        when(genreService.insert(any(CreateGenreRequestDto.class))).thenReturn(createdGenre);

        mockMvc.perform(post("/api/v1/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(new CreatedEntityDto(10L))));

        verify(genreService).insert(any(CreateGenreRequestDto.class));
    }

    @Test
    void shouldUpdateGenre() throws Exception {
        UpdateGenreRequestDto request = new UpdateGenreRequestDto("Updated Genre");
        GenreDto updatedGenre = new GenreDto(1L, "Updated Genre");

        when(genreService.update(eq(1L), any(UpdateGenreRequestDto.class))).thenReturn(updatedGenre);

        mockMvc.perform(put("/api/v1/genre/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedGenre)));

        verify(genreService).update(eq(1L), any(UpdateGenreRequestDto.class));
    }

    @Test
    void shouldReturn404WhenGenreNotFound() throws Exception {
        when(genreService.findById(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/genre/{id}", 100L))
                .andExpect(status().isNotFound());

        verify(genreService).findById(100L);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        when(genreService.findAll()).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isInternalServerError());

        verify(genreService).findAll();
    }

    @Test
    void shouldReturn400WhenInvalidCreateInput() throws Exception {
        CreateGenreRequestDto invalidRequest = new CreateGenreRequestDto("");

        mockMvc.perform(post("/api/v1/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInvalidUpdateInput() throws Exception {
        UpdateGenreRequestDto invalidRequest = new UpdateGenreRequestDto("");

        mockMvc.perform(put("/api/v1/genre/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}