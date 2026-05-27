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
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({BookServiceImpl.class, FixturesLoader.class})
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @Test
    void shouldFindAllBooks() {
        var books = bookService.findAll();
        assertThat(books).hasSize(3);
        books.forEach(book -> {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getGenres()).isNotEmpty();
        });
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldFindBookById(Book expected) {
        var optional = bookService.findById(expected.getId());
        assertThat(optional).isPresent();
        assertThat(optional.get())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getBooksForInsert")
    void shouldInsertNewBook(Book expected) {
        Set<String> genreIds = expected.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        var saved = bookService.insert(
                expected.getTitle(),
                expected.getAuthor().getId(),
                genreIds
        );
        assertThat(saved)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getBooksForUpdate")
    void shouldUpdateBook(Book expected) {
        Set<String> genreIds = expected.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        var saved = bookService.update(
                expected.getId(),
                expected.getTitle(),
                expected.getAuthor().getId(),
                genreIds
        );
        assertThat(saved)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getBookIdsForDelete")
    void shouldDeleteBook(String id) {
        assertThat(commentRepository.findByBookId(id)).isNotEmpty();
        bookService.deleteById(id);
        assertThat(bookService.findById(id)).isNotPresent();
    }

    private static List<Book> getDbBooks() {
        return List.of(
                createBook("1", "BookTitle_1", "1", List.of("1", "2")),
                createBook("2", "BookTitle_2", "2", List.of("3", "4")),
                createBook("3", "BookTitle_3", "3", List.of("5", "6"))
        );
    }

    private static Book createBook(String id, String title, String authorId, List<String> genreIds) {
        Author author = new Author(authorId, "Author_" + authorId);
        List<Genre> genres = genreIds.stream()
                .map(gid -> new Genre(gid, "Genre_" + gid))
                .collect(Collectors.toList());
        return new Book(id, title, author, genres);
    }

    private static List<Book> getBooksForInsert() {
        return List.of(
                createBook(null, "BookTitle_4", "1", List.of("1", "2")),
                createBook(null, "BookTitle_5", "2", List.of("3"))
        );
    }

    private static List<Book> getBooksForUpdate() {
        return List.of(
                createBook("1", "BookTitle_1_updated", "2", List.of("3", "4")),
                createBook("2", "BookTitle_2_updated", "3", List.of("5", "6"))
        );
    }

    private static List<String> getBookIdsForDelete() {
        return List.of("1", "3");
    }
}