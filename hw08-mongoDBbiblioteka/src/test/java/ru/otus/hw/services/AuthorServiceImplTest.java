package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({AuthorServiceImpl.class, FixturesLoader.class})
class AuthorServiceImplTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @Test
    void shouldFindAllAuthors() {
        var authors = authorService.findAll();
        var expected = FixturesLoader.getExpectedAuthors();
        assertThat(authors).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldFindAuthorById(Author expected) {
        var optional = authorService.findById(expected.getId());
        assertThat(optional).isPresent();
        assertThat(optional.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getAuthorsForInsert")
    void shouldInsertNewAuthor(Author expected) {
        var saved = authorService.insert(expected.getFullName());
        assertThat(saved).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getAuthorsForUpdate")
    void shouldUpdateAuthor(Author expected) {
        var saved = authorService.update(expected.getId(), expected.getFullName());
        assertThat(saved).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getAuthorIdsForDelete")
    void shouldDeleteAuthor(String id) {
        assertThat(authorService.findById(id)).isNotEmpty();
        authorService.deleteById(id);
        assertThat(authorService.findById(id)).isNotPresent();
    }

    private static List<Author> getDbAuthors() {
        return FixturesLoader.getExpectedAuthors();
    }

    private static List<Author> getAuthorsForInsert() {
        return List.of(
                new Author(null, "Author_4"),
                new Author(null, "Author_5")
        );
    }

    private static List<Author> getAuthorsForUpdate() {
        return List.of(
                new Author("2", "Author_2_updated"),
                new Author("3", "Author_3_updated")
        );
    }

    private static List<String> getAuthorIdsForDelete() {
        return List.of("1", "3");
    }
}