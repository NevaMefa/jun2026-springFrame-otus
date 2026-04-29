package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookServiceIT {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Test
    void shouldNotThrowLazyExceptionWhenAccessingAuthorAndGenres() {
        Book book = bookService.findById(1L).orElseThrow();

        assertDoesNotThrow(() -> {
            book.getAuthor().getFullName();
            book.getGenres().forEach(g -> g.getName());
        });
    }

    @Test
    void shouldNotThrowLazyExceptionWhenAccessingBookFromComment() {
        var comments = commentService.findByBookId(1L);
        assertThat(comments).isNotEmpty();

        assertDoesNotThrow(() -> {
            for (var comment : comments) {
                comment.getBook().getTitle();
            }
        });
    }
}