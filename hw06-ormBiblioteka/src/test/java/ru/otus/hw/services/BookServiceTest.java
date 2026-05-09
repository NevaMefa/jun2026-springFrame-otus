package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({JpaAuthorRepository.class, JpaBookRepository.class, JpaGenreRepository.class, BookServiceImpl.class})
public class BookServiceTest {

    @Autowired
    private BookServiceImpl bookService;

    @Test
    void shouldNotThrowLazyExceptionWhenFindById() {
        var optionalBook = bookService.findById(1L);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
        assertThat(optionalBook).isPresent();
        Book book = optionalBook.get();
        assertDoesNotThrow(() -> {
            book.getAuthor().getFullName();
            book.getGenres().size();
        });
    }

    @Test
    void shouldNotThrowLazyExceptionWhenFindAll() {
        List<Book> books = bookService.findAll();
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
        assertThat(books).isNotEmpty();
        for (Book book : books) {
            assertDoesNotThrow(() -> {
                book.getAuthor().getFullName();
                book.getGenres().size();
            });
        }
    }

}