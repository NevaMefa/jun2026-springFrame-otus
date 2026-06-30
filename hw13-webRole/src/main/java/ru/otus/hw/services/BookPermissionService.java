package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.hw.repositories.BookRepository;

@Service
@RequiredArgsConstructor
public class BookPermissionService {

    private final BookRepository bookRepository;

    public boolean canReadBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            return false;
        }
        return hasPermission("READ");
    }

    public boolean canEditBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            return false;
        }
        return hasPermission("WRITE");
    }

    public boolean canDeleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            return false;
        }
        return hasPermission("DELETE");
    }

    private boolean hasPermission(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
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