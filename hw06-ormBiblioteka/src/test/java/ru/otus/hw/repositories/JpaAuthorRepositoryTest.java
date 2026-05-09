package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuthorRepository.class)
class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository authorRepo;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindAll() {
        List<Author> authors = authorRepo.findAll();
        assertThat(authors).isNotEmpty();
        assertThat(authors.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldFindById() {
        Author author = new Author(0, "Test Author");
        em.persist(author);
        em.flush();

        Optional<Author> found = authorRepo.findById(author.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test Author");
    }

    @Test
    void shouldSaveNewAuthor() {
        Author newAuthor = new Author(0, "New Author");
        Author saved = authorRepo.save(newAuthor);
        assertThat(saved.getId()).isPositive();
        assertThat(saved.getFullName()).isEqualTo("New Author");

        Author found = em.find(Author.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getFullName()).isEqualTo("New Author");
    }

    @Test
    void shouldUpdateAuthor() {
        Author author = new Author(0, "Old Name");
        em.persist(author);
        em.flush();

        author.setFullName("Updated Name");
        Author updated = authorRepo.save(author);
        assertThat(updated.getFullName()).isEqualTo("Updated Name");

        Author found = em.find(Author.class, author.getId());
        assertThat(found.getFullName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldDeleteAuthor() {
        Author author = new Author(0, "To Delete");
        em.persist(author);
        em.flush();
        Long id = author.getId();
        assertThat(em.find(Author.class, id)).isNotNull();

        authorRepo.deleteById(id);
        em.flush();
        assertThat(em.find(Author.class, id)).isNull();
    }
}