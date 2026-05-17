package ru.otus.hw.repositories;

import org.hibernate.Hibernate;
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
        assertThat(saved.getGenres()).hasSize(1);
        assertThat(saved.getGenres().get(0).getName()).isEqualTo("Test Genre");
    }

    @Test
    void shouldSaveBookWithMultipleGenres() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Genre genre1 = new Genre(0, "Genre A");
        Genre genre2 = new Genre(0, "Genre B");
        em.persist(genre1);
        em.persist(genre2);
        em.flush();

        Book book = new Book(0, "Multi-Genre Book", author, List.of(genre1, genre2));
        Book saved = bookRepo.save(book);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getGenres()).hasSize(2);
        assertThat(saved.getGenres()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre A", "Genre B");
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
        assertThat(updated.getGenres().get(0).getName()).isEqualTo("Updated Genre");
    }

    @Test
    void shouldUpdateBookGenres() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Genre oldGenre = new Genre(0, "Old Genre");
        Genre newGenre = new Genre(0, "New Genre");
        em.persist(oldGenre);
        em.persist(newGenre);
        em.flush();

        Book book = new Book(0, "Book", author, new ArrayList<>(List.of(oldGenre)));
        em.persist(book);
        em.flush();

        book.setGenres(new ArrayList<>(List.of(newGenre)));
        Book updated = bookRepo.save(book);

        assertThat(updated.getGenres()).hasSize(1);
        assertThat(updated.getGenres().get(0).getName()).isEqualTo("New Genre");
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

    @Test
    void shouldLoadGenresLazilyWhenNoEntityGraph() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Genre genre = new Genre(0, "Genre");
        em.persist(genre);
        Book book = new Book(0, "Book", author, List.of(genre));
        em.persist(book);
        em.flush();
        em.clear();

        Book found = em.find(Book.class, book.getId());
        assertThat(Hibernate.isInitialized(found.getGenres())).isFalse();
    }

    @Test
    void shouldLoadAuthorAndGenresWhenUsingEntityGraphFindById() {
        Author author = new Author(0, "Author");
        em.persist(author);
        Genre genre1 = new Genre(0, "Genre1");
        Genre genre2 = new Genre(0, "Genre2");
        em.persist(genre1);
        em.persist(genre2);
        Book book = new Book(0, "Book", author, List.of(genre1, genre2));
        em.persist(book);
        em.flush();
        em.clear();

        var foundOpt = bookRepo.findById(book.getId());
        assertThat(foundOpt).isPresent();
        Book found = foundOpt.get();

        assertThat(Hibernate.isInitialized(found.getAuthor())).isTrue();
        assertThat(Hibernate.isInitialized(found.getGenres())).isTrue();
        assertThat(found.getGenres()).hasSize(2);
    }
}