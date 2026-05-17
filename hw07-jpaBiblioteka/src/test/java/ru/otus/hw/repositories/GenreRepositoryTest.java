package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Test
    void shouldFindAllByIds() {
        var genres = repository.findAllByIds(Set.of(1L, 3L, 5L));
        assertThat(genres).hasSize(3);
        assertThat(genres).extracting(Genre::getName).containsExactlyInAnyOrder("Genre_1", "Genre_3", "Genre_5");
    }
}