package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(long bookId) {
        List<Comment> comments = commentRepository.findByBookId(bookId);
        comments.forEach(comment -> comment.getBook().getTitle());
        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        comment.ifPresent(c -> c.getBook().getTitle());
        return comment;
    }

    @Override
    @Transactional
    public Comment insert(String text, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + bookId));
        Comment comment = new Comment(0, book, text);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(long id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id " + id));
        comment.setText(text);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}