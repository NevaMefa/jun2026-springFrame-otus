package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("book-entity-graph");
        Map<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.fetchgraph", entityGraph);
        Book book = em.find(Book.class, id, hints);
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = loadBooksWithAuthors();
        if (books.isEmpty()) {
            return books;
        }

        Set<Long> bookIds = books.stream().map(Book::getId).collect(Collectors.toSet());
        Map<Long, List<Genre>> bookGenresMap = loadGenresForBooks(bookIds);
        attachGenresToBooks(books, bookGenresMap);
        return books;
    }

    private List<Book> loadBooksWithAuthors() {
        return em.createQuery(
                        "select distinct b from Book b left join fetch b.author", Book.class)
                .getResultList();
    }

    private Map<Long, List<Genre>> loadGenresForBooks(Set<Long> bookIds) {
        List<Object[]> relations = em.createNativeQuery(
                        "select book_id, genre_id from books_genres where book_id in (:ids)")
                .setParameter("ids", bookIds)
                .getResultList();
        Set<Long> genreIds = relations.stream()
                .map(row -> ((Number) row[1]).longValue())
                .collect(Collectors.toSet());
        Map<Long, Genre> genreMap = em.createQuery("select g from Genre g where g.id in :ids", Genre.class)
                .setParameter("ids", genreIds)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        Map<Long, List<Genre>> bookGenresMap = new HashMap<>();
        for (Object[] row : relations) {
            Long bookId = ((Number) row[0]).longValue();
            Long genreId = ((Number) row[1]).longValue();
            Genre genre = genreMap.get(genreId);
            if (genre != null) {
                bookGenresMap.computeIfAbsent(bookId, k -> new ArrayList<>()).add(genre);
            }
        }
        return bookGenresMap;
    }

    private void attachGenresToBooks(List<Book> books, Map<Long, List<Genre>> bookGenresMap) {
        books.forEach(book -> {
            List<Genre> genres = bookGenresMap.getOrDefault(book.getId(), Collections.emptyList());
            book.setGenres(genres);
        });
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public void deleteById(long id) {
        findById(id).ifPresent(em::remove);
    }
}