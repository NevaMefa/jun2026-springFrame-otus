package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(key = "cbbid", value = "Find comments by book id")
    public String findCommentsByBookId(String bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "cbid", value = "Find comment by id")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(key = "cins", value = "Insert comment")
    public String insertComment(String text, String bookId) {
        var saved = commentService.insert(text, bookId);
        return commentConverter.commentToString(saved);
    }

    @ShellMethod(key = "cupd", value = "Update comment")
    public String updateComment(String id, String text, String bookId) {
        var saved = commentService.update(id, text, bookId);
        return commentConverter.commentToString(saved);
    }

    @ShellMethod(key = "cdel", value = "Delete comment")
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }
}