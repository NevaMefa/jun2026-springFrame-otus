package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaGenreRepository.class)
class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository genreRepo;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindAll() {
        Genre genre1 = new Genre(0, "Genre 1");
        Genre genre2 = new Genre(0, "Genre 2");
        em.persist(genre1);
        em.persist(genre2);
        em.flush();

        List<Genre> genres = genreRepo.findAll();
        assertThat(genres).hasSize(2);
    }

    @Test
    void shouldFindByIds() {
        Genre genre1 = new Genre(0, "Genre A");
        Genre genre2 = new Genre(0, "Genre B");
        em.persist(genre1);
        em.persist(genre2);
        em.flush();

        List<Genre> genres = genreRepo.findAllByIds(Set.of(genre1.getId(), genre2.getId()));
        assertThat(genres).hasSize(2);
    }
}