package ru.otus.hw.repositories;

import java.util.List;

public interface BookRepositoryCustom {
    void pullGenreFromAllBooks(String genreId);

    void updateAuthorFullName(String authorId, String newFullName);

    void deleteByAuthorId(String authorId);

    List<String> findBookIdsByAuthorId(String authorId);
}