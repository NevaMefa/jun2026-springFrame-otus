package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookCascadeDeleteEventListener extends AbstractMongoEventListener<Book> {

    private final MongoOperations mongoOperations;

    @Override
    public void onAfterDelete(AfterDeleteEvent<Book> event) {
        Document doc = event.getSource();
        Object bookId = doc.get("_id");
        if (bookId != null) {
            Query query = new Query(Criteria.where("book._id").is(bookId.toString()));
            mongoOperations.remove(query, Comment.class);
            log.debug("Deleted comments for book: {}", bookId);
        }
    }
}