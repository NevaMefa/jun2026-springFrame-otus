package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.mappers.BookMapper;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({BookMapper.class, BookServiceImpl.class})
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void shouldFindById() {
        var optionalBook = bookService.findById(1L);
        assertThat(optionalBook).isPresent();
        BookDto book = optionalBook.get();
        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getAuthor()).isNotNull();
        assertThat(book.getGenres()).isNotEmpty();
    }

    @Test
    void shouldFindAll() {
        List<BookDto> books = bookService.findAll();
        assertThat(books).isNotEmpty();
        for (BookDto book : books) {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getGenres()).isNotNull();
        }
    }

    @Test
    void shouldInsert() {
        BookDto newBook = bookService.insert("New Book", 1L, Set.of(1L, 2L));
        assertThat(newBook.getId()).isPositive();
        assertThat(newBook.getTitle()).isEqualTo("New Book");
        assertThat(newBook.getAuthor().getId()).isEqualTo(1L);
        assertThat(newBook.getGenres()).hasSize(2);
    }

    @Test
    void shouldUpdate() {
        BookDto updated = bookService.update(1L, "Updated Title", 2L, Set.of(3L, 4L));
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getAuthor().getId()).isEqualTo(2L);
        assertThat(updated.getGenres()).hasSize(2);
    }
}