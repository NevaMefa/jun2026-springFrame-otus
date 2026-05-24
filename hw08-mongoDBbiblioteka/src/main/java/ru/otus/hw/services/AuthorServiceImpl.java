package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Author> findAll() {
        return Streamable.of(authorRepository.findAll()).toList();
    }

    @Override
    public Optional<Author> findById(String id) {
        return authorRepository.findById(id);
    }

    @Override
    public Author insert(String fullName) {
        var author = new Author(null, fullName);
        return authorRepository.save(author);
    }

    @Override
    public Author update(String id, String fullName) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
        author.setFullName(fullName);
        return authorRepository.save(author);
    }

    @Override
    public void deleteById(String id) {
        Query booksQuery = new Query(Criteria.where("author.id").is(id));
        List<Book> books = mongoTemplate.find(booksQuery, Book.class);

        for (Book book : books) {
            Query commentsQuery = new Query(Criteria.where("book._id").is(book.getId()));
            mongoTemplate.remove(commentsQuery, Comment.class);
        }

        for (Book book : books) {
            mongoTemplate.remove(book);
        }

        Query authorQuery = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(authorQuery, Author.class);
    }
}