package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class AuthorCommands {
    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find author by id", key = "abid")
    public String findAuthorById(String id) {
        return authorService.findById(id)
                .map(authorConverter::authorToString)
                .orElse("Author with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert author", key = "ains")
    public String insertAuthor(String fullName) {
        var saved = authorService.insert(fullName);
        return authorConverter.authorToString(saved);
    }

    @ShellMethod(value = "Update author", key = "aupd")
    public String updateAuthor(String id, String fullName) {
        var saved = authorService.update(id, fullName);
        return authorConverter.authorToString(saved);
    }

    @ShellMethod(value = "Delete author by id", key = "adel")
    public void deleteAuthor(String id) {
        authorService.deleteById(id);
    }
}