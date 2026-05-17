package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindAllAuthors() {
        List<Author> authors = repository.findAll();
        assertThat(authors).hasSize(3);
    }

    @Test
    void shouldFindAuthorById() {
        Author author = repository.findById(1L).orElse(null);
        assertThat(author).isNotNull();
        assertThat(author.getFullName()).isEqualTo("Author_1");
    }

    @Test
    void shouldInsertAuthor() {
        Author newAuthor = new Author(0, "Author_4");
        Author saved = repository.save(newAuthor);
        assertThat(saved.getId()).isPositive();
        Author found = em.find(Author.class, saved.getId());
        assertThat(found.getFullName()).isEqualTo("Author_4");
    }

    @Test
    void shouldUpdateAuthor() {
        Author author = repository.findById(1L).get();
        author.setFullName("Updated_Author");
        repository.save(author);
        em.flush();
        em.clear();
        Author updated = repository.findById(1L).get();
        assertThat(updated.getFullName()).isEqualTo("Updated_Author");
    }

    @Test
    void shouldDeleteAuthor() {
        repository.deleteById(3L);
        assertThat(repository.findById(3L)).isNotPresent();
    }
}