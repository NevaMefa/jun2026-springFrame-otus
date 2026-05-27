package ru.otus.hw.fixtures;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

public class FixturesLoader {
    private final MongoTemplate mongoTemplate;

    public FixturesLoader(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void load() {
        createCollections();
        insertAuthors();
        insertGenres();
        insertBooks();
        insertComments();
    }

    private void createCollections() {
        if (!mongoTemplate.collectionExists("authors")) {
            mongoTemplate.createCollection("authors");
        }
        if (!mongoTemplate.collectionExists("genres")) {
            mongoTemplate.createCollection("genres");
        }
        if (!mongoTemplate.collectionExists("books")) {
            mongoTemplate.createCollection("books");
        }
        if (!mongoTemplate.collectionExists("comments")) {
            mongoTemplate.createCollection("comments");
        }
    }

    private void insertAuthors() {
        mongoTemplate.insert(new Author("1", "Author_1"), "authors");
        mongoTemplate.insert(new Author("2", "Author_2"), "authors");
        mongoTemplate.insert(new Author("3", "Author_3"), "authors");
    }

    private void insertGenres() {
        for (int i = 1; i <= 6; i++) {
            mongoTemplate.insert(new Genre(String.valueOf(i), "Genre_" + i), "genres");
        }
    }

    private void insertBooks() {
        Author a1 = mongoTemplate.findById("1", Author.class, "authors");
        Author a2 = mongoTemplate.findById("2", Author.class, "authors");
        Author a3 = mongoTemplate.findById("3", Author.class, "authors");
        Genre g1 = mongoTemplate.findById("1", Genre.class, "genres");
        Genre g2 = mongoTemplate.findById("2", Genre.class, "genres");
        Genre g3 = mongoTemplate.findById("3", Genre.class, "genres");
        Genre g4 = mongoTemplate.findById("4", Genre.class, "genres");
        Genre g5 = mongoTemplate.findById("5", Genre.class, "genres");
        Genre g6 = mongoTemplate.findById("6", Genre.class, "genres");

        Book b1 = new Book("1", "BookTitle_1", a1, new ArrayList<>());
        b1.addGenre(g1);
        b1.addGenre(g2);
        Book b2 = new Book("2", "BookTitle_2", a2, new ArrayList<>());
        b2.addGenre(g3);
        b2.addGenre(g4);
        Book b3 = new Book("3", "BookTitle_3", a3, new ArrayList<>());
        b3.addGenre(g5);
        b3.addGenre(g6);

        mongoTemplate.insert(b1, "books");
        mongoTemplate.insert(b2, "books");
        mongoTemplate.insert(b3, "books");
    }

    private void insertComments() {
        Book b1 = mongoTemplate.findById("1", Book.class, "books");
        Book b2 = mongoTemplate.findById("2", Book.class, "books");
        Book b3 = mongoTemplate.findById("3", Book.class, "books");
        mongoTemplate.insert(new Comment("1", b1, "Comment_1"), "comments");
        mongoTemplate.insert(new Comment("2", b1, "Comment_2"), "comments");
        mongoTemplate.insert(new Comment("3", b2, "Comment_3"), "comments");
        mongoTemplate.insert(new Comment("4", b3, "Comment_4"), "comments");
    }

    public void purge() {
        dropIfExists("comments");
        dropIfExists("books");
        dropIfExists("genres");
        dropIfExists("authors");
    }

    private void dropIfExists(String name) {
        if (mongoTemplate.collectionExists(name)) {
            mongoTemplate.dropCollection(name);
        }
    }

    public static List<Author> getExpectedAuthors() {
        return List.of(
                new Author("1", "Author_1"),
                new Author("2", "Author_2"),
                new Author("3", "Author_3")
        );
    }

    public static List<Genre> getExpectedGenres() {
        return List.of(
                new Genre("1", "Genre_1"), new Genre("2", "Genre_2"),
                new Genre("3", "Genre_3"), new Genre("4", "Genre_4"),
                new Genre("5", "Genre_5"), new Genre("6", "Genre_6")
        );
    }

    public static List<Book> getExpectedBooks() {
        Author author1 = new Author("1", "Author_1");
        Author author2 = new Author("2", "Author_2");
        Author author3 = new Author("3", "Author_3");

        Genre genre1 = new Genre("1", "Genre_1");
        Genre genre2 = new Genre("2", "Genre_2");
        Genre genre3 = new Genre("3", "Genre_3");
        Genre genre4 = new Genre("4", "Genre_4");
        Genre genre5 = new Genre("5", "Genre_5");
        Genre genre6 = new Genre("6", "Genre_6");

        return List.of(
                createBook("1", "BookTitle_1", author1, List.of(genre1, genre2)),
                createBook("2", "BookTitle_2", author2, List.of(genre3, genre4)),
                createBook("3", "BookTitle_3", author3, List.of(genre5, genre6))
        );
    }

    private static Book createBook(String id, String title, Author author, List<Genre> genres) {
        Book book = new Book(id, title, author, new ArrayList<>());
        genres.forEach(book::addGenre);
        return book;
    }
}