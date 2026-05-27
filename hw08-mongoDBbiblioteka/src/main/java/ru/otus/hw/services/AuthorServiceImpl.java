package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

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
        var updatedAuthor = authorRepository.save(author);

        Query query = Query.query(Criteria.where("author.id").is(id));
        Update update = new Update().set("author.fullName", fullName);
        mongoTemplate.updateMulti(query, update, Book.class);

        return updatedAuthor;
    }

    @Override
    public void deleteById(String id) {
        Iterable<Book> booksIterable = bookRepository.findAll();
        List<Book> books = StreamSupport.stream(booksIterable.spliterator(), false)
                .filter(b -> id.equals(b.getAuthor().getId()))
                .collect(Collectors.toList());

        if (!books.isEmpty()) {
            for (Book book : books) {
                commentRepository.deleteByBook(book);
            }
            bookRepository.deleteAll(books);
        }

        authorRepository.deleteById(id);
    }
}