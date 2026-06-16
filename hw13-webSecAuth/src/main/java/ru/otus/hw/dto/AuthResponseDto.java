package ru.otus.hw.dto;

public record AuthResponseDto(
        String token,
        String username,
        String role
) {
}