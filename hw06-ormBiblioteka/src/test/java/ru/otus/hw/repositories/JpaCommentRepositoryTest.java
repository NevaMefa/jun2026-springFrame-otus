package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCommentRepository.class)
class JpaCommentRepositoryTest {

    @Autowired
    private JpaCommentRepository commentRepo;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindCommentsByBookId() {
        Author author = new Author(0, "Test Author");
        em.persist(author);
        Book book = new Book(0, "Test Book", author, List.of());
        em.persist(book);
        Comment c1 = new Comment(0, "Nice comment", book);
        Comment c2 = new Comment(0, "Bad comment", book);
        em.persist(c1);
        em.persist(c2);
        em.flush();

        List<Comment> comments = commentRepo.findByBookId(book.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getText()).isIn("Nice comment", "Bad comment");
        assertThat(comments.get(0).getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void shouldFindCommentById() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        Comment comment = new Comment(0, "Find me", book);
        em.persist(comment);
        em.flush();

        Optional<Comment> found = commentRepo.findById(comment.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getText()).isEqualTo("Find me");
        assertThat(found.get().getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void shouldSaveNewComment() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        em.flush();

        Comment newComment = new Comment(0, "New comment", book);
        Comment saved = commentRepo.save(newComment);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getText()).isEqualTo("New comment");
        assertThat(saved.getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    void shouldUpdateComment() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        Comment comment = new Comment(0, "Old text", book);
        em.persist(comment);
        em.flush();

        comment.setText("Updated text");
        Comment updated = commentRepo.save(comment);

        assertThat(updated.getId()).isEqualTo(comment.getId());
        assertThat(updated.getText()).isEqualTo("Updated text");
    }

    @Test
    void shouldDeleteCommentById() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        Comment comment = new Comment(0, "Delete me", book);
        em.persist(comment);
        em.flush();

        Long id = comment.getId();
        assertThat(commentRepo.findById(id)).isPresent();

        commentRepo.deleteById(id);
        em.flush();

        assertThat(commentRepo.findById(id)).isEmpty();
    }
}