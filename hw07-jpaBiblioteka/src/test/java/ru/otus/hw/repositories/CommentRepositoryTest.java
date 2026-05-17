package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindCommentsByBookId() {
        var comments = repository.findByBookId(1L);
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText).containsExactlyInAnyOrder("Отличная книга!", "Очень хорошая");
    }

    @Test
    void shouldInsertComment() {
        Book book = em.find(Book.class, 1L);
        Comment comment = new Comment(0, book, "New comment");
        Comment saved = repository.save(comment);
        assertThat(saved.getId()).isPositive();
        assertThat(repository.findByBookId(1L)).hasSize(3);
    }

    @Test
    void shouldDeleteComment() {
        repository.deleteById(1L);
        assertThat(repository.findById(1L)).isNotPresent();
    }
}