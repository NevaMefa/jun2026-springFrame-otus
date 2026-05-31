package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.util.Streamable;
import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.BookRepositoryCustomImpl;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({AuthorServiceImpl.class, GenreServiceImpl.class, BookServiceImpl.class,
        CommentServiceImpl.class, FixturesLoader.class, BookRepositoryCustomImpl.class})
public class CascadeDeleteTest {

    @Autowired
    private AuthorService authorService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private BookRepository bookRepository;
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
    void shouldDeleteBooksAndCommentsWhenDeletingAuthor() {
        String authorId = "1";

        List<Book> allBooks = Streamable.of(bookRepository.findAll()).toList();
        assertThat(allBooks).extracting(b -> b.getAuthor().getId()).contains(authorId);

        List<String> bookIds = allBooks.stream()
                .filter(b -> authorId.equals(b.getAuthor().getId()))
                .map(Book::getId)
                .toList();

        for (String bookId : bookIds) {
            assertThat(commentRepository.findByBookId(bookId)).isNotEmpty();
        }

        authorService.deleteById(authorId);

        List<Book> remainingBooks = Streamable.of(bookRepository.findAll()).toList();
        boolean authorBooksExist = remainingBooks.stream()
                .anyMatch(b -> authorId.equals(b.getAuthor().getId()));
        assertThat(authorBooksExist).isFalse();

        for (String bookId : bookIds) {
            assertThat(commentRepository.findByBookId(bookId)).isEmpty();
        }
    }

    @Test
    void shouldRemoveGenreFromBooksWhenDeletingGenre() {
        String genreId = "1";
        Book book = bookRepository.findById("1").orElseThrow();

        List<String> genreIdsBefore = book.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        assertThat(genreIdsBefore).contains(genreId);

        genreService.deleteById(genreId);

        Book updated = bookRepository.findById("1").orElseThrow();
        List<String> genreIdsAfter = updated.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        assertThat(genreIdsAfter).doesNotContain(genreId);
        assertThat(genreService.findById(genreId)).isNotPresent();
    }
}