package ru.otus.hw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

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
        return authorRepository.save(new Author(null, fullName));
    }

    @Override
    public Author update(String id, String fullName) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
        author.setFullName(fullName);
        Author updated = authorRepository.save(author);
        bookRepository.updateAuthorFullName(id, fullName);
        return updated;
    }

    @Override
    public void deleteById(String id) {
        List<String> bookIds = bookRepository.findBookIdsByAuthorId(id);

        if (!bookIds.isEmpty()) {
            for (String bookId : bookIds) {
                commentRepository.deleteByBookId(bookId);
            }
        }

        bookRepository.deleteByAuthorId(id);

        authorRepository.deleteById(id);
    }
}