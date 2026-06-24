package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Genre;

public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {

    @Query(value = "{ 'name': ?0 }", exists = true)
    Mono<Boolean> existsByName(String name);
}