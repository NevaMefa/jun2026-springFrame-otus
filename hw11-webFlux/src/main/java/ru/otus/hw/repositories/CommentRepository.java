package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

    Flux<Comment> findByBookId(String bookId);

    Mono<Void> deleteByBookId(String bookId);

    @Query("{ 'book.id': ?0 }")
    @Update("{ '$set': { 'book.title': ?1 } }")
    Mono<Void> updateBookTitleInComments(String bookId, String newTitle);
}