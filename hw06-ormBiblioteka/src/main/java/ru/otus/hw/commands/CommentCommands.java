package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(key = "cbid", value = "Find comments by book id")
    public String findCommentsByBookId(long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "cins", value = "Insert comment")
    public String insertComment(String text, long bookId) {
        var comment = commentService.insert(text, bookId);
        return commentConverter.commentToString(comment);
    }

    @ShellMethod(key = "cupd", value = "Update comment")
    public String updateComment(long id, String text) {
        var comment = commentService.update(id, text);
        return commentConverter.commentToString(comment);
    }

    @ShellMethod(key = "cdel", value = "Delete comment")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}
