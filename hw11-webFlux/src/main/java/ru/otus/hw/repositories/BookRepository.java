package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {

    @Query(value = "{ 'author.id': ?0 }", exists = true)
    Mono<Boolean> existsByAuthorId(String authorId);

    @Query(value = "{ 'genres.id': ?0 }", exists = true)
    Mono<Boolean> existsByGenreId(String genreId);

    @Query("{ 'author.id': ?0 }")
    @Update("{ '$set': { 'author.fullName': ?1 } }")
    Mono<Void> updateAuthorInBooks(String authorId, String authorFullName);

    @Query("{ 'genres.id': ?0 }")
    @Update("{ '$set': { 'genres.$.name': ?1 } }")
    Mono<Void> updateGenreInBooks(String genreId, String genreName);

    @Query("{ 'genres.id': ?0 }")
    @Update("{ '$pull': { 'genres': { 'id': ?0 } } }")
    Mono<Void> removeGenreFromBooks(String genreId);

    @Query(value = "{ 'title': ?0 }", exists = true)
    Mono<Boolean> existsByTitle(String title);
}