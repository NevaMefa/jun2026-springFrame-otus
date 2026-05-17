package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCommentRepository.class)
@Transactional(propagation = Propagation.REQUIRES_NEW)
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
        Comment c1 = new Comment(0, book, "Nice comment");
        Comment c2 = new Comment(0, book, "Bad comment");
        em.persist(c1);
        em.persist(c2);
        em.flush();

        List<Comment> comments = commentRepo.findByBookId(book.getId());
        assertThat(comments).hasSize(2);
    }

    @Test
    void shouldFindCommentById() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        Comment comment = new Comment(0, book, "Find me");
        em.persist(comment);
        em.flush();

        var found = commentRepo.findById(comment.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getText()).isEqualTo("Find me");
    }

    @Test
    void shouldSaveNewComment() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        em.flush();

        Comment newComment = new Comment(0, book, "New comment");
        Comment saved = commentRepo.save(newComment);
        assertThat(saved.getId()).isPositive();
        assertThat(saved.getText()).isEqualTo("New comment");
    }

    @Test
    void shouldUpdateComment() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        Comment comment = new Comment(0, book, "Old text");
        em.persist(comment);
        em.flush();

        comment.setText("Updated text");
        Comment updated = commentRepo.save(comment);
        assertThat(updated.getText()).isEqualTo("Updated text");
    }

    @Test
    void shouldDeleteCommentById() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Book book = new Book(0, "Book", author, List.of());
        em.persist(book);
        Comment comment = new Comment(0, book, "Delete me");
        em.persist(comment);
        em.flush();

        Long id = comment.getId();
        assertThat(em.find(Comment.class, id)).isNotNull();

        commentRepo.deleteById(id);
        em.flush();

        assertThat(em.find(Comment.class, id)).isNull();
    }
}