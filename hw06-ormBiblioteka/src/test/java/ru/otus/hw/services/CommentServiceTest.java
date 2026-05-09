package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({JpaBookRepository.class, JpaCommentRepository.class, CommentServiceImpl.class})
public class CommentServiceTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void shouldNotThrowLazyExceptionWhenFindById() {
        var optionalComment = commentService.findById(1L);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
        assertThat(optionalComment).isPresent();
        assertDoesNotThrow(() -> optionalComment.get().getBook().getTitle());
    }

    @Test
    void shouldNotThrowLazyExceptionWhenFindByBookId() {
        List<Comment> comments = commentService.findByBookId(1L);
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
        assertThat(comments).isNotEmpty();
        for (Comment comment : comments) {
            assertDoesNotThrow(() -> comment.getBook().getTitle());
        }
    }
}