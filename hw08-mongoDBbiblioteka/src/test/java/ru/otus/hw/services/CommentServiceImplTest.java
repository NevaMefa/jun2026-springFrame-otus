package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.fixtures.FixturesLoader;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({CommentServiceImpl.class, FixturesLoader.class})
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private FixturesLoader fixturesLoader;

    @BeforeEach
    void setUp() {
        fixturesLoader.purge();
        fixturesLoader.load();
    }

    @ParameterizedTest
    @MethodSource("getExpectedCommentsByBookId")
    void shouldFindCommentsByBookId(String bookId, List<Comment> expected) {
        var comments = commentService.findByBookId(bookId);
        assertThat(comments)
                .usingRecursiveComparison()
                .ignoringFields("book")
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getExpectedComments")
    void shouldFindCommentById(Comment expected) {
        var optional = commentService.findById(expected.getId());
        assertThat(optional).isPresent();
        assertThat(optional.get())
                .usingRecursiveComparison()
                .ignoringFields("book")
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getCommentsForInsert")
    void shouldInsertNewComment(Comment expected) {
        var saved = commentService.insert(
                expected.getText(),
                expected.getBook().getId()
        );
        assertThat(saved)
                .usingRecursiveComparison()
                .ignoringFields("id", "book")
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"999", "1000"})
    void shouldThrowExceptionOnInsertWhenBookNotFound(String nonExistentBookId) {
        assertThatThrownBy(() -> commentService.insert("Comment_100", nonExistentBookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id %s not found".formatted(nonExistentBookId));
    }

    @ParameterizedTest
    @MethodSource("getCommentsForUpdate")
    void shouldUpdateComment(Comment expected) {
        var saved = commentService.update(
                expected.getId(),
                expected.getText(),
                expected.getBook().getId()
        );
        assertThat(saved)
                .usingRecursiveComparison()
                .ignoringFields("book")
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"999", "1000"})
    void shouldThrowExceptionOnUpdateWhenBookNotFound(String nonExistentBookId) {
        assertThatThrownBy(() -> commentService.update("1", "Updated text", nonExistentBookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id %s not found".formatted(nonExistentBookId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void shouldDeleteComment(String id) {
        assertThat(commentService.findById(id)).isPresent();
        commentService.deleteById(id);
        assertThat(commentService.findById(id)).isNotPresent();
    }

    // Вспомогательные методы для данных
    private static List<Object[]> getExpectedCommentsByBookId() {
        return List.of(
                new Object[]{"1", List.of(
                        new Comment("1", null, "Comment_1"),
                        new Comment("2", null, "Comment_2")
                )},
                new Object[]{"2", List.of(
                        new Comment("3", null, "Comment_3")
                )},
                new Object[]{"3", List.of(
                        new Comment("4", null, "Comment_4")
                )}
        );
    }

    private static List<Comment> getExpectedComments() {
        return List.of(
                new Comment("1", null, "Comment_1"),
                new Comment("2", null, "Comment_2"),
                new Comment("3", null, "Comment_3"),
                new Comment("4", null, "Comment_4")
        );
    }

    private static List<Comment> getCommentsForInsert() {
        var book1 = new Book("1", null, null, null);
        var book2 = new Book("2", null, null, null);
        return List.of(
                new Comment(null, book1, "Comment_5"),
                new Comment(null, book2, "Comment_6")
        );
    }

    private static List<Comment> getCommentsForUpdate() {
        var book2 = new Book("2", null, null, null);
        var book3 = new Book("3", null, null, null);
        return List.of(
                new Comment("1", book2, "Comment_1_updated"),
                new Comment("3", book3, "Comment_3_updated")
        );
    }
}