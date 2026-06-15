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

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.dto.UpdateCommentRequestDto;
import ru.otus.hw.services.CommentService;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldGetCommentsByBookId() throws Exception {
        List<CommentDto> expectedComments = List.of(
                new CommentDto(1L, "Comment 1", 1L, "Book 1"),
                new CommentDto(2L, "Comment 2", 1L, "Book 1")
        );

        when(commentService.findByBookId(1L)).thenReturn(expectedComments);

        mockMvc.perform(get("/api/v1/comment")
                        .param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedComments)));

        verify(commentService).findByBookId(1L);
    }

    @Test
    void shouldGetCommentById() throws Exception {
        CommentDto expectedComment = new CommentDto(1L, "Comment 1", 1L, "Book 1");
        when(commentService.findById(1L)).thenReturn(Optional.of(expectedComment));

        mockMvc.perform(get("/api/v1/comment/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedComment)));

        verify(commentService).findById(1L);
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comment/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(commentService).deleteById(1L);
    }

    @Test
    void shouldCreateComment() throws Exception {
        CreateCommentDto request = new CreateCommentDto("New comment", 1L);
        CommentDto createdComment = new CommentDto(10L, "New comment", 1L, "Book 1");

        when(commentService.insert(any(CreateCommentDto.class))).thenReturn(createdComment);

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdComment)));

        verify(commentService).insert(any(CreateCommentDto.class));
    }

    @Test
    void shouldUpdateComment() throws Exception {
        UpdateCommentRequestDto request = new UpdateCommentRequestDto("Updated comment");
        CommentDto updatedComment = new CommentDto(1L, "Updated comment", 1L, "Book 1");

        when(commentService.update(any(UpdateCommentDto.class))).thenReturn(updatedComment);

        mockMvc.perform(put("/api/v1/comment/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedComment)));

        verify(commentService).update(any(UpdateCommentDto.class));
    }

    @Test
    void shouldReturn404WhenCommentNotFound() throws Exception {
        when(commentService.findById(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/comment/{id}", 100L))
                .andExpect(status().isNotFound());

        verify(commentService).findById(100L);
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws Exception {
        when(commentService.findByBookId(1L)).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/api/v1/comment").param("bookId", "1"))
                .andExpect(status().isInternalServerError());

        verify(commentService).findByBookId(1L);
    }

    @Test
    void shouldReturn400WhenCreateWithInvalidInput() throws Exception {
        CreateCommentDto invalidRequest = new CreateCommentDto("", 1L);

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreateWithNullBookId() throws Exception {
        CreateCommentDto invalidRequest = new CreateCommentDto("Valid text", null);

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdateWithInvalidInput() throws Exception {
        UpdateCommentRequestDto invalidRequest = new UpdateCommentRequestDto("");

        mockMvc.perform(put("/api/v1/comment/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenBookIdMissing() throws Exception {
        mockMvc.perform(get("/api/v1/comment"))
                .andExpect(status().isBadRequest());
    }
}