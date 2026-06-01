package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.CreateGenreRequestDto;
import ru.otus.hw.dto.UpdateGenreRequestDto;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    List<GenreDto> findAll();

    Optional<GenreDto> findById(long id);

    GenreDto insert(CreateGenreRequestDto requestDto);  // изменено

    GenreDto update(long id, UpdateGenreRequestDto requestDto);  // изменено

    void deleteById(long id);
}