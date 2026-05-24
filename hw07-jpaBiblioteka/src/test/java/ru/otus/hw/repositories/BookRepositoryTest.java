package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindAllBooksWithAuthorOnly() {
        List<Book> books = repository.findAll();
        assertThat(books).hasSize(3);
        books.forEach(book -> assertThat(book.getAuthor().getFullName()).isNotBlank());
    }

    @Test
    void shouldFindBookByIdWithGenres() {
        Book book = repository.findById(1L).orElseThrow();
        assertThat(book.getGenres()).hasSize(2);
        assertThat(book.getAuthor()).isNotNull();
    }

    @Test
    void shouldInsertBook() {
        Author author = em.find(Author.class, 1L);
        Genre genre1 = em.find(Genre.class, 1L);
        Genre genre2 = em.find(Genre.class, 2L);
        Book newBook = new Book(0, "New Book", author, List.of(genre1, genre2));
        Book saved = repository.save(newBook);
        assertThat(saved.getId()).isPositive();
        Book found = repository.findById(saved.getId()).get();
        assertThat(found.getTitle()).isEqualTo("New Book");
        assertThat(found.getGenres()).hasSize(2);
    }

    @Test
    void shouldUpdateBook() {
        Book book = repository.findById(1L).get();
        book.setTitle("Updated Title");
        repository.save(book);
        em.flush();
        em.clear();
        Book updated = repository.findById(1L).get();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void shouldDeleteBook() {
        repository.deleteById(1L);
        em.flush();
        em.clear();

        assertThat(repository.findById(1L)).isNotPresent();

        long commentsCount = em.getEntityManager()
                .createQuery("SELECT COUNT(c) FROM Comment c WHERE c.book.id = 1", Long.class)
                .getSingleResult();
        assertThat(commentsCount).isZero();
    }
}