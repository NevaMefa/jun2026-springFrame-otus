package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public void pullGenreFromAllBooks(String genreId) {
        Query query = Query.query(Criteria.where("genres._id").is(genreId));
        Update update = new Update().pull("genres", Query.query(Criteria.where("_id").is(genreId)));
        mongoTemplate.updateMulti(query, update, Book.class);
    }

    @Override
    public void updateAuthorFullName(String authorId, String newFullName) {
        Query query = Query.query(Criteria.where("author._id").is(authorId));
        Update update = new Update().set("author.fullName", newFullName);
        mongoTemplate.updateMulti(query, update, Book.class);
    }

    @Override
    public void deleteByAuthorId(String authorId) {
        Query query = Query.query(Criteria.where("author._id").is(authorId));
        mongoTemplate.remove(query, Book.class);
    }

    @Override
    public List<String> findBookIdsByAuthorId(String authorId) {
        Query query = Query.query(Criteria.where("author._id").is(authorId));
        query.fields().include("_id");
        return mongoTemplate.find(query, Book.class).stream()
                .map(Book::getId)
                .collect(Collectors.toList());
    }
}