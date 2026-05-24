package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Component
@RequiredArgsConstructor
public class GenreCascadeDeleteEventListener extends AbstractMongoEventListener<Genre> {
    private final MongoOperations mongoOperations;

    @Override
    public void onAfterDelete(AfterDeleteEvent<Genre> event) {
        Document doc = event.getSource();
        Object genreId = doc.get("_id");
        if (genreId != null) {
            Query query = Query.query(Criteria.where("genres.id").is(genreId.toString()));
            Update update = new Update().pull("genres", Query.query(Criteria.where("id").is(genreId.toString())));
            mongoOperations.updateMulti(query, update, Book.class);
        }
    }
}