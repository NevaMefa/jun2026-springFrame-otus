package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    @Autowired
    private JpaBookRepository bookRepo;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindBookById() {
        Author author = new Author(0, "Test Author");
        em.persist(author);
        Book book = new Book(0, "Test Book", author, new ArrayList<>());
        em.persist(book);
        em.flush();

        var found = bookRepo.findById(book.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldFindAllBooks() {
        List<Book> books = bookRepo.findAll();
        assertThat(books).isNotEmpty();
        assertThat(books.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldSaveNewBook() {
        Author author = new Author(0, "New Author");
        em.persist(author);
        Genre genre = new Genre(0, "Test Genre");
        em.persist(genre);
        em.flush();

        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        Book newBook = new Book(0, "Brand New Book", author, genres);
        Book saved = bookRepo.save(newBook);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getTitle()).isEqualTo("Brand New Book");
    }

    @Test
    void shouldUpdateBook() {
        Author author = new Author(0, "Old Author");
        em.persist(author);
        Book book = new Book(0, "Old Title", author, new ArrayList<>());
        em.persist(book);
        em.flush();

        Author newAuthor = new Author(0, "Updated Author");
        em.persist(newAuthor);
        Genre genre = new Genre(0, "Updated Genre");
        em.persist(genre);
        em.flush();

        book.setTitle("Updated Title");
        book.setAuthor(newAuthor);
        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        book.setGenres(genres);

        Book updated = bookRepo.save(book);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getAuthor().getFullName()).isEqualTo("Updated Author");
        assertThat(updated.getGenres()).hasSize(1);
    }

    @Test
    void shouldDeleteBookById() {
        Author author = new Author(0, "To Delete");
        em.persist(author);
        Book book = new Book(0, "To Delete", author, new ArrayList<>());
        em.persist(book);
        em.flush();

        Long id = book.getId();
        assertThat(em.find(Book.class, id)).isNotNull();

        bookRepo.deleteById(id);
        em.flush();
        assertThat(em.find(Book.class, id)).isNull();
    }
}