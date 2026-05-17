package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.repositories.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaBookRepository.class, JpaCommentRepository.class, CommentMapper.class, CommentServiceImpl.class})
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void shouldFindById() {
        var optionalComment = commentService.findById(1L);
        assertThat(optionalComment).isPresent();
        CommentDto comment = optionalComment.get();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getBookId()).isPositive();
    }

    @Test
    void shouldFindByBookId() {
        List<CommentDto> comments = commentService.findByBookId(1L);
        assertThat(comments).isNotEmpty();
        for (CommentDto comment : comments) {
            assertThat(comment.getBookId()).isEqualTo(1L);
        }
    }

    @Test
    void shouldInsert() {
        CommentDto newComment = commentService.insert("Test comment", 1L);
        assertThat(newComment.getId()).isPositive();
        assertThat(newComment.getText()).isEqualTo("Test comment");
        assertThat(newComment.getBookId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdate() {
        CommentDto updated = commentService.update(1L, "Updated comment");
        assertThat(updated.getText()).isEqualTo("Updated comment");
    }
}