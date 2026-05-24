package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.CreateAuthorRequestDto;
import ru.otus.hw.dto.UpdateAuthorRequestDto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<AuthorDto> findAll();

    Optional<AuthorDto> findById(long id);

    AuthorDto insert(CreateAuthorRequestDto requestDto);  // изменено

    AuthorDto update(long id, UpdateAuthorRequestDto requestDto);  // изменено

    void deleteById(long id);
}