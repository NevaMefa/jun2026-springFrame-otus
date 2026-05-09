package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void shouldNotThrowLazyExceptionWhenFindById() {
        var comment = commentService.findById(1L).orElseThrow();
        assertDoesNotThrow(() -> comment.getBook().getTitle());
    }

    @Test
    void shouldNotThrowLazyExceptionWhenFindByBookId() {
        var comments = commentService.findByBookId(1L);
        assertDoesNotThrow(() -> {
            for (var comment : comments) {
                comment.getBook().getTitle();
            }
        });
    }

    @Test
    void shouldNotThrowLazyExceptionWhenInsert() {
        var comment = commentService.insert("Test comment", 1L);
        assertDoesNotThrow(() -> comment.getBook().getTitle());
    }

    @Test
    void shouldNotThrowLazyExceptionWhenUpdate() {
        var comment = commentService.update(1L, "Updated comment");
        assertDoesNotThrow(() -> comment.getBook().getTitle());
    }
}