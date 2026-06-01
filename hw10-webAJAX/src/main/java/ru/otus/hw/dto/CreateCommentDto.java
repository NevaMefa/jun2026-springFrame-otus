package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentDto(
        @NotBlank(message = "Text cannot be blank")
        @Size(min = 2, max = 1024, message = "Text must be between 2 and 1024 characters")
        String text,

        @NotNull(message = "Book ID cannot be null")
        Long bookId
) {
}