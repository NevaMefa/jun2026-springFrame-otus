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
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

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
        return genreRepository.save(new Genre(null, name));
    }

    @Override
    public Genre update(String id, String name) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(id)));

        String oldName = genre.getName();
        genre.setName(name);
        Genre updated = genreRepository.save(genre);

        if (!oldName.equals(name)) {
            Query query = Query.query(Criteria.where("genres._id").is(id));
            Update update = new Update().set("genres.$.name", name);
            long updatedCount = mongoTemplate.updateMulti(query, update, Book.class).getModifiedCount();
            log.debug("Updated genre name in {} books", updatedCount);
        }

        return updated;
    }

    @Override
    public void deleteById(String id) {
        bookRepository.pullGenreFromAllBooks(id);
        genreRepository.deleteById(id);
    }
}