package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        List<Comment> comments = em.createQuery(
                        "select c from Comment c left join fetch c.book where c.id = :id",
                        Comment.class)
                .setParameter("id", id)
                .getResultList();
        return comments.stream().findFirst();
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        return em.createQuery(
                        "select c from Comment c left join fetch c.book where c.book.id = :bookId",
                        Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        } else {
            return em.merge(comment);
        }
    }

    @Override
    public void deleteById(long id) {
        findById(id).ifPresent(em::remove);
    }
}
