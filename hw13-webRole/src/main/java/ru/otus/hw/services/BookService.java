package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.UpdateBookRequestDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<BookDto> findById(long id);

    List<BookDto> findAll();

    BookDto insert(CreateBookRequestDto requestDto);

    BookDto update(long id, UpdateBookRequestDto requestDto);

    void deleteById(long id);
}