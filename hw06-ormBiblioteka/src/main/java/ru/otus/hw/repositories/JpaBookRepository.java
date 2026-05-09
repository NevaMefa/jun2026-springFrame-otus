package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return em.createQuery(
                        "select distinct b from Book b left join fetch b.author", Book.class)
                .getResultList();
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