package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateCommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found with id: " + bookId);
        }
        return commentRepository.findByBookId(bookId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CommentDto insert(CreateCommentDto dto) {
        Book book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + dto.bookId()));
        Comment comment = new Comment(0, book, dto.text());
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CommentDto update(UpdateCommentDto dto) {
        Comment comment = commentRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id " + dto.id()));
        comment.setText(dto.text());
        Comment updated = commentRepository.save(comment);
        return commentMapper.toDto(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteById(long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}