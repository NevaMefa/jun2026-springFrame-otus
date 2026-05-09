package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void shouldNotThrowLazyExceptionWhenFindById() {
        Book book = bookService.findById(1L).orElseThrow();
        assertDoesNotThrow(() -> {
            book.getAuthor().getFullName();
            book.getGenres().forEach(g -> g.getName());
        });
    }

    @Test
    void shouldNotThrowLazyExceptionWhenFindAll() {
        List<Book> books = bookService.findAll();
        assertDoesNotThrow(() -> {
            for (Book book : books) {
                book.getAuthor().getFullName();
                book.getGenres().forEach(g -> g.getName());
            }
        });
    }

    @Test
    void shouldNotThrowLazyExceptionWhenInsert() {
        Book book = bookService.insert("New Book", 1L, Set.of(1L, 2L));
        assertDoesNotThrow(() -> {
            book.getAuthor().getFullName();
            book.getGenres().forEach(g -> g.getName());
        });
    }

    @Test
    void shouldNotThrowLazyExceptionWhenUpdate() {
        Book book = bookService.update(1L, "Updated Title", 2L, Set.of(3L, 4L));
        assertDoesNotThrow(() -> {
            book.getAuthor().getFullName();
            book.getGenres().forEach(g -> g.getName());
        });
    }
}