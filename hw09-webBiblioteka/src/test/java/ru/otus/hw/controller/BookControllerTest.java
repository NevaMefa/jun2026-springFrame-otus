package ru.otus.hw.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CreateBookRequestDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UpdateBookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldShowAllBooks() throws Exception {
        var expectedBooks = getBookDtos();

        when(bookService.findAll()).thenReturn(expectedBooks);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/all"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", expectedBooks));

        verify(bookService).findAll();
    }

    @Test
    void shouldShowOneBook() throws Exception {
        BookDto expectedBook = getBookDto(1L);
        when(bookService.findById(1L)).thenReturn(Optional.of(expectedBook));

        List<CommentDto> expectedComments = getCommentDtos(1L);
        when(commentService.findByBookId(1L)).thenReturn(expectedComments);

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("books/show"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", expectedBook))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("comments", expectedComments));

        verify(bookService).findById(1L);
    }

    @Test
    void shouldShowDeletePage() throws Exception {
        BookDto expectedBook = getBookDto(1L);
        when(bookService.findById(1L)).thenReturn(Optional.of(expectedBook));

        mockMvc.perform(get("/books/{id}/delete", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("books/delete"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", expectedBook));

        verify(bookService).findById(1L);
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/{bookId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteById(1L);
    }

    @Test
    void shouldShowCreatePage() throws Exception {
        var expectedAuthors = getAuthorDtos();
        when(authorService.findAll()).thenReturn(expectedAuthors);

        var expectedGenres = getGenreDtos();
        when(genreService.findAll()).thenReturn(expectedGenres);

        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/create"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attribute("authors", expectedAuthors))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("genres", expectedGenres));

        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void shouldCreateBook() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "New Book", 1L, Set.of(1L, 2L)
        );
        BookDto savedBook = getBookDto(4L);
        savedBook.setTitle("New Book");

        when(bookService.insert(any(CreateBookRequestDto.class))).thenReturn(savedBook);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).insert(any(CreateBookRequestDto.class));
    }

    @Test
    void shouldShowEditPage() throws Exception {
        BookDto expectedBook = getBookDto(1L);
        when(bookService.findById(1L)).thenReturn(Optional.of(expectedBook));

        var expectedAuthors = getAuthorDtos();
        when(authorService.findAll()).thenReturn(expectedAuthors);

        var expectedGenres = getGenreDtos();
        when(genreService.findAll()).thenReturn(expectedGenres);

        mockMvc.perform(get("/books/{bookId}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", expectedBook))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attribute("authors", expectedAuthors))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("genres", expectedGenres));

        verify(bookService).findById(1L);
        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void shouldUpdateBook() throws Exception {
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto(
                "Updated Title", 2L, Set.of(2L, 3L)
        );
        BookDto updatedBook = getBookDto(1L);
        updatedBook.setTitle("Updated Title");

        when(bookService.update(anyLong(), any(UpdateBookRequestDto.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/books/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Updated Title")
                        .param("authorId", "2")
                        .param("genreIds", "2", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).update(anyLong(), any(UpdateBookRequestDto.class));
    }

    @Test
    void shouldGet404() throws Exception {
        when(bookService.findById(100L)).thenThrow(new EntityNotFoundException("Book not found"));
        mockMvc.perform(get("/books/{id}", 100L))
                .andExpect(status().isNotFound());
        verify(bookService).findById(100L);
    }

    @Test
    void shouldGet500() throws Exception {
        when(bookService.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));
        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isInternalServerError());
        verify(bookService).findById(1L);
    }

    @Test
    void shouldGet400() throws Exception {
        mockMvc.perform(post("/books")
                        .param("title", "")
                        .param("authorId", "1"))
                .andExpect(status().isBadRequest());
    }


    private static List<BookDto> getBookDtos() {
        return List.of(
                getBookDto(1L),
                getBookDto(2L),
                getBookDto(3L)
        );
    }

    private static BookDto getBookDto(long id) {
        BookDto book = new BookDto();
        book.setId(id);
        book.setTitle("BookTitle_" + id);
        book.setAuthor(getAuthorDto(id));

        if (id == 1L) {
            book.setGenres(List.of(
                    new GenreDto(1L, "Genre_1"),
                    new GenreDto(2L, "Genre_2")
            ));
        } else if (id == 2L) {
            book.setGenres(List.of(
                    new GenreDto(3L, "Genre_3"),
                    new GenreDto(4L, "Genre_4")
            ));
        } else {
            book.setGenres(List.of(
                    new GenreDto(5L, "Genre_5"),
                    new GenreDto(6L, "Genre_6")
            ));
        }
        return book;
    }

    private static AuthorDto getAuthorDto(long id) {
        return new AuthorDto(id, "Author_" + id);
    }

    private static List<AuthorDto> getAuthorDtos() {
        return List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2"),
                new AuthorDto(3L, "Author_3")
        );
    }

    private static List<GenreDto> getGenreDtos() {
        return List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2"),
                new GenreDto(3L, "Genre_3"),
                new GenreDto(4L, "Genre_4"),
                new GenreDto(5L, "Genre_5"),
                new GenreDto(6L, "Genre_6")
        );
    }

    private static List<CommentDto> getCommentDtos(long bookId) {
        if (bookId == 1L) {
            return List.of(
                    new CommentDto(1L, "Comment_1", 1L, "BookTitle_1"),
                    new CommentDto(2L, "Comment_2", 1L, "BookTitle_1")
            );
        } else if (bookId == 2L) {
            return List.of(
                    new CommentDto(3L, "Comment_3", 2L, "BookTitle_2")
            );
        } else {
            return List.of(
                    new CommentDto(4L, "Comment_4", 3L, "BookTitle_3")
            );
        }
    }
}