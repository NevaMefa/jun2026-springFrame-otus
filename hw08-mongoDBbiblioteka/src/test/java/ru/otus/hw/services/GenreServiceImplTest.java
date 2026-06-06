package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({GenreServiceImpl.class, FixturesLoader.class})
class GenreServiceImplTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @Test
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        var expected = FixturesLoader.getExpectedGenres();
        assertThat(genres).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getDbGenres")
    void shouldFindGenreById(Genre expected) {
        var optional = genreService.findById(expected.getId());
        assertThat(optional).isPresent();
        assertThat(optional.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getGenresForInsert")
    void shouldInsertNewGenre(Genre expected) {
        var saved = genreService.insert(expected.getName());
        assertThat(saved).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getGenresForUpdate")
    void shouldUpdateGenre(Genre expected) {
        var saved = genreService.update(expected.getId(), expected.getName());
        assertThat(saved).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getGenreIdsForDelete")
    void shouldDeleteGenre(String id) {
        assertThat(genreService.findById(id)).isNotEmpty();
        genreService.deleteById(id);
        assertThat(genreService.findById(id)).isNotPresent();
    }

    private static List<Genre> getDbGenres() {
        return FixturesLoader.getExpectedGenres();
    }

    private static List<Genre> getGenresForInsert() {
        return List.of(
                new Genre(null, "Genre_7"),
                new Genre(null, "Genre_8")
        );
    }

    private static List<Genre> getGenresForUpdate() {
        return List.of(
                new Genre("2", "Genre_2_updated"),
                new Genre("4", "Genre_4_updated")
        );
    }

    private static List<String> getGenreIdsForDelete() {
        return List.of("1", "5");
    }
}