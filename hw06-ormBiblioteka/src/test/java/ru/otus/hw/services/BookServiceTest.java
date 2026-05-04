package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void shouldNotThrowLazyExceptionWhenAccessingAuthorAndGenres() {
        Book book = bookService.findById(1L).orElseThrow();
        assertDoesNotThrow(() -> {
            book.getAuthor().getFullName();
            book.getGenres().forEach(g -> g.getName());
        });
    }
}