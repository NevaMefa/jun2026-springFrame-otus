package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorCascadeDeleteEventListener extends AbstractMongoEventListener<Author> {

    private final MongoOperations mongoOperations;

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Author> event) {
        Document document = event.getSource();

        Object authorId = document.get("_id");

        if (authorId == null) {
            return;
        }

        String authorIdStr = authorId.toString();

        Query booksQuery = new Query(Criteria.where("author.id").is(authorIdStr));
        List<Book> books = mongoOperations.find(booksQuery, Book.class);

        for (Book book : books) {
            Query commentsQuery = new Query(Criteria.where("book._id").is(book.getId()));
            mongoOperations.remove(commentsQuery, Comment.class);
        }

        mongoOperations.remove(booksQuery, Book.class);

        log.debug("Deleted author: {}, along with {} books and their comments", authorIdStr, books.size());
    }
}