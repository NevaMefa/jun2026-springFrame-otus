package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository genreRepository;

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = genreRepository.findAll();
        var expectedGenres = getDbGenres();

        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @DisplayName("должен загружать жанры по списку id")
    @Test
    void shouldReturnCorrectGenresByIds() {
        var ids = Set.of(1L, 3L, 5L);
        var actualGenres = genreRepository.findAllByIds(ids);
        var expectedGenres = getDbGenres().stream()
                .filter(g -> ids.contains(g.getId()))
                .toList();

        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    @DisplayName("должен возвращать пустой список, если передан пустой набор id")
    @Test
    void shouldReturnEmptyListWhenIdsSetIsEmpty() {
        var actualGenres = genreRepository.findAllByIds(Set.of());
        assertThat(actualGenres).isEmpty();
    }

    @DisplayName("должен возвращать только существующие жанры, если некоторые id отсутствуют")
    @Test
    void shouldReturnOnlyExistingGenres() {
        var ids = Set.of(1L, 100L, 3L);
        var actualGenres = genreRepository.findAllByIds(ids);
        var expectedGenres = getDbGenres().stream()
                .filter(g -> g.getId() == 1L || g.getId() == 3L)
                .toList();

        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }
}