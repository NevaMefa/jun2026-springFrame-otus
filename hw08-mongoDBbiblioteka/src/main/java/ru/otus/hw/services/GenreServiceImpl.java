package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Genre> findAll() {
        return Streamable.of(genreRepository.findAll()).toList();
    }

    @Override
    public Optional<Genre> findById(String id) {
        return genreRepository.findById(id);
    }

    @Override
    public Genre insert(String name) {
        var genre = new Genre(null, name);
        return genreRepository.save(genre);
    }

    @Override
    public Genre update(String id, String name) {
        var genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(id)));

        String oldName = genre.getName();
        genre.setName(name);
        var updatedGenre = genreRepository.save(genre);

        if (!oldName.equals(name)) {
            Query query = Query.query(Criteria.where("genres.id").is(id));
            Update update = new Update().set("genres.$.name", name);
            long updatedCount = mongoTemplate.updateMulti(query, update, Book.class).getModifiedCount();
            log.debug("Updated genre name in {} books", updatedCount);
        }

        return updatedGenre;
    }

    @Override
    public void deleteById(String id) {
        Query bookQuery = new Query(Criteria.where("genres.id").is(id));
        Update update = new Update().pull("genres", Query.query(Criteria.where("id").is(id)));
        mongoTemplate.updateMulti(bookQuery, update, Book.class);

        Query genreQuery = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(genreQuery, Genre.class);
    }
}