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
        List<Genre> genres = genreRepo.findAll();
        assertThat(genres).isNotEmpty();
        assertThat(genres.size()).isGreaterThanOrEqualTo(2);
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

    @Test
    void shouldSaveNewGenre() {
        Genre newGenre = new Genre(0, "New Genre");
        Genre saved = genreRepo.save(newGenre);
        assertThat(saved.getId()).isPositive();
        assertThat(saved.getName()).isEqualTo("New Genre");

        Genre found = em.find(Genre.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("New Genre");
    }

    @Test
    void shouldUpdateGenre() {
        Genre genre = new Genre(0, "Old Name");
        em.persist(genre);
        em.flush();

        genre.setName("Updated Name");
        Genre updated = genreRepo.save(genre);
        assertThat(updated.getName()).isEqualTo("Updated Name");

        Genre found = em.find(Genre.class, genre.getId());
        assertThat(found.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldDeleteGenre() {
        Genre genre = new Genre(0, "To Delete");
        em.persist(genre);
        em.flush();
        Long id = genre.getId();
        assertThat(em.find(Genre.class, id)).isNotNull();

        genreRepo.deleteById(id);
        em.flush();
        assertThat(em.find(Genre.class, id)).isNull();
    }
}