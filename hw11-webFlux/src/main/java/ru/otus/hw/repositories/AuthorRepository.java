package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

    @Query(value = "{ 'fullName': ?0 }", exists = true)
    Mono<Boolean> existsByFullName(String fullName);
}