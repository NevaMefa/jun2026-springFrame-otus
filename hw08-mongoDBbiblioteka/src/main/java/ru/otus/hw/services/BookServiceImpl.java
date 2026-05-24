package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return Streamable.of(bookRepository.findAll()).toList();
    }

    @Override
    public Book insert(String title, String authorId, Set<String> genresIds) {
        Author author = getAuthorById(authorId);
        List<Genre> genres = getGenresByIds(genresIds);
        return bookRepository.save(new Book(null, title, author, genres));
    }

    @Override
    public Book update(String id, String title, String authorId, Set<String> genresIds) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        book.setTitle(title);
        book.setAuthor(getAuthorById(authorId));
        book.setGenres(getGenresByIds(genresIds));
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Book.class);
    }

    private Author getAuthorById(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
    }

    private List<Genre> getGenresByIds(Set<String> genresIds) {
        Iterable<Genre> iterable = genreRepository.findAllById(genresIds);
        List<Genre> genres = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        if (genres.size() != genresIds.size()) {
            throw new EntityNotFoundException("One or more genres not found");
        }
        return genres;
    }
}