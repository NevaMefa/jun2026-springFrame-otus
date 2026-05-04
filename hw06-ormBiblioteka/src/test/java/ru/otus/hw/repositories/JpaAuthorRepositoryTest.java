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
        Author author1 = new Author(0, "Author 1");
        Author author2 = new Author(0, "Author 2");
        em.persist(author1);
        em.persist(author2);
        em.flush();

        List<Author> authors = authorRepo.findAll();
        assertThat(authors).hasSize(2);
    }

    @Test
    void shouldFindById() {
        Author author = new Author(0, "Test");
        em.persist(author);
        em.flush();

        Optional<Author> found = authorRepo.findById(author.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test");
    }
}