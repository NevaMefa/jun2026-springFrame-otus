package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;


@Service
@RequiredArgsConstructor
public class BookPermissionService {

    private final BookRepository bookRepository;

    public boolean canReadBook(Long bookId) {
        return hasPermission(bookId, "READ");
    }

    public boolean canEditBook(Long bookId) {
        return hasPermission(bookId, "WRITE");
    }

    public boolean canDeleteBook(Long bookId) {
        return hasPermission(bookId, "DELETE");
    }

    private boolean hasPermission(Long bookId, String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return false;
        }

        if (isAdmin(auth)) {
            return true;
        }

        if (permission.equals("READ")) {
            return isUser(auth);
        }

        return false;
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isUser(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_USER"));
    }
}