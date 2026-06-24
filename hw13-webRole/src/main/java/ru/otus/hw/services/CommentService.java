package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(long id);

    List<CommentDto> findByBookId(long bookId);

    CommentDto insert(CreateCommentDto dto);

    CommentDto update(UpdateCommentDto dto);

    void deleteById(long id);
}