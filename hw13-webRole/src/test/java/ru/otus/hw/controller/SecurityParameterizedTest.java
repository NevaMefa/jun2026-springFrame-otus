package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.otus.hw.config.MethodSecurityConfig;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.dto.*;
import ru.otus.hw.services.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        BookRestController.class,
        AuthorRestController.class,
        GenreRestController.class,
        CommentRestController.class
})
@Import({SecurityConfig.class, MethodSecurityConfig.class})
class SecurityParameterizedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookPermissionService bookPermissionService;  // ← ДОБАВЛЕН

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // ===== Book mocks =====
        BookDto book = new BookDto();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor(new AuthorDto(1L, "Test Author"));
        book.setGenres(List.of(new GenreDto(1L, "Test Genre")));
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(bookService.findAll()).thenReturn(List.of(book));
        when(bookService.insert(any(CreateBookRequestDto.class))).thenReturn(book);
        when(bookService.update(anyLong(), any(UpdateBookRequestDto.class))).thenReturn(book);

        when(bookPermissionService.canReadBook(anyLong())).thenReturn(true);
        when(bookPermissionService.canEditBook(anyLong())).thenReturn(true);
        when(bookPermissionService.canDeleteBook(anyLong())).thenReturn(true);

        AuthorDto author = new AuthorDto(1L, "Test Author");
        when(authorService.findById(1L)).thenReturn(Optional.of(author));
        when(authorService.findAll()).thenReturn(List.of(author));
        when(authorService.insert(any(CreateAuthorRequestDto.class))).thenReturn(author);
        when(authorService.update(anyLong(), any(UpdateAuthorRequestDto.class))).thenReturn(author);

        GenreDto genre = new GenreDto(1L, "Test Genre");
        when(genreService.findById(1L)).thenReturn(Optional.of(genre));
        when(genreService.findAll()).thenReturn(List.of(genre));
        when(genreService.insert(any(CreateGenreRequestDto.class))).thenReturn(genre);
        when(genreService.update(anyLong(), any(UpdateGenreRequestDto.class))).thenReturn(genre);

        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setBookId(1L);
        when(commentService.findById(1L)).thenReturn(Optional.of(comment));
        when(commentService.findByBookId(1L)).thenReturn(List.of(comment));
        when(commentService.insert(any(CreateCommentDto.class))).thenReturn(comment);
        when(commentService.update(any(UpdateCommentDto.class))).thenReturn(comment);
    }

    @ParameterizedTest
    @MethodSource("provideEndpointsWithoutAuth")
    void shouldRedirectToLoginWhenAccessingWithoutAuth(String url, String method, String requestBody) throws Exception {
        mockMvc.perform(buildRequest(method, url, requestBody))
                .andExpect(status().is3xxRedirection());
    }

    @ParameterizedTest
    @MethodSource("provideEndpointsUserRead")
    @WithMockUser(roles = "USER")
    void userCanReadResources(String url, String method, String requestBody) throws Exception {
        mockMvc.perform(buildRequest(method, url, requestBody))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideEndpointsUserWrite")
    @WithMockUser(roles = "USER")
    void userCannotWriteResources(String url, String method, String requestBody) throws Exception {
        mockMvc.perform(buildRequest(method, url, requestBody))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideEndpointsAdminRead")
    @WithMockUser(roles = "ADMIN")
    void adminCanReadResources(String url, String method, String requestBody) throws Exception {
        mockMvc.perform(buildRequest(method, url, requestBody))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideEndpointsAdminWrite")
    @WithMockUser(roles = "ADMIN")
    void adminCanWriteResources(String url, String method, String requestBody, int expectedStatus) throws Exception {
        mockMvc.perform(buildRequest(method, url, requestBody))
                .andExpect(status().is(expectedStatus));
    }


    private static Stream<Arguments> provideEndpointsWithoutAuth() {
        return Stream.of(
                Arguments.of("/api/v1/book", "GET", ""),
                Arguments.of("/api/v1/book/1", "GET", ""),
                Arguments.of("/api/v1/book", "POST", getBookBody()),
                Arguments.of("/api/v1/book/1", "PUT", getBookBody()),
                Arguments.of("/api/v1/book/1", "DELETE", ""),
                Arguments.of("/api/v1/author", "GET", ""),
                Arguments.of("/api/v1/author/1", "GET", ""),
                Arguments.of("/api/v1/author", "POST", getAuthorBody()),
                Arguments.of("/api/v1/author/1", "PUT", getAuthorBody()),
                Arguments.of("/api/v1/author/1", "DELETE", ""),
                Arguments.of("/api/v1/genre", "GET", ""),
                Arguments.of("/api/v1/genre/1", "GET", ""),
                Arguments.of("/api/v1/genre", "POST", getGenreBody()),
                Arguments.of("/api/v1/genre/1", "PUT", getGenreBody()),
                Arguments.of("/api/v1/genre/1", "DELETE", ""),
                Arguments.of("/api/v1/comment?bookId=1", "GET", ""),
                Arguments.of("/api/v1/comment/1", "GET", ""),
                Arguments.of("/api/v1/comment", "POST", getCommentBody()),
                Arguments.of("/api/v1/comment/1", "PUT", getCommentBody()),
                Arguments.of("/api/v1/comment/1", "DELETE", "")
        );
    }

    private static Stream<Arguments> provideEndpointsUserRead() {
        return Stream.of(
                Arguments.of("/api/v1/book", "GET", ""),
                Arguments.of("/api/v1/book/1", "GET", ""),
                Arguments.of("/api/v1/author", "GET", ""),
                Arguments.of("/api/v1/author/1", "GET", ""),
                Arguments.of("/api/v1/genre", "GET", ""),
                Arguments.of("/api/v1/genre/1", "GET", ""),
                Arguments.of("/api/v1/comment?bookId=1", "GET", ""),
                Arguments.of("/api/v1/comment/1", "GET", "")
        );
    }

    private static Stream<Arguments> provideEndpointsUserWrite() {
        return Stream.of(
                Arguments.of("/api/v1/book", "POST", getBookBody()),
                Arguments.of("/api/v1/book/1", "PUT", getBookBody()),
                Arguments.of("/api/v1/book/1", "DELETE", ""),
                Arguments.of("/api/v1/author", "POST", getAuthorBody()),
                Arguments.of("/api/v1/author/1", "PUT", getAuthorBody()),
                Arguments.of("/api/v1/author/1", "DELETE", ""),
                Arguments.of("/api/v1/genre", "POST", getGenreBody()),
                Arguments.of("/api/v1/genre/1", "PUT", getGenreBody()),
                Arguments.of("/api/v1/genre/1", "DELETE", ""),
                Arguments.of("/api/v1/comment/1", "PUT", getCommentBody()),
                Arguments.of("/api/v1/comment/1", "DELETE", "")
        );
    }

    private static Stream<Arguments> provideEndpointsAdminRead() {
        return provideEndpointsUserRead();
    }

    private static Stream<Arguments> provideEndpointsAdminWrite() {
        return Stream.of(
                Arguments.of("/api/v1/book", "POST", getBookBody(), 201),
                Arguments.of("/api/v1/book/1", "PUT", getBookBody(), 200),
                Arguments.of("/api/v1/book/1", "DELETE", "", 204),
                Arguments.of("/api/v1/author", "POST", getAuthorBody(), 201),
                Arguments.of("/api/v1/author/1", "PUT", getAuthorBody(), 200),
                Arguments.of("/api/v1/author/1", "DELETE", "", 204),
                Arguments.of("/api/v1/genre", "POST", getGenreBody(), 201),
                Arguments.of("/api/v1/genre/1", "PUT", getGenreBody(), 200),
                Arguments.of("/api/v1/genre/1", "DELETE", "", 204),
                Arguments.of("/api/v1/comment", "POST", getCommentBody(), 201),
                Arguments.of("/api/v1/comment/1", "PUT", getCommentBody(), 200),
                Arguments.of("/api/v1/comment/1", "DELETE", "", 204)
        );
    }


    private static String getBookBody() {
        return """
                {
                    "title": "Test Book",
                    "authorId": 1,
                    "genreIds": [1]
                }
                """;
    }

    private static String getAuthorBody() {
        return """
                {
                    "fullName": "Test Author"
                }
                """;
    }

    private static String getGenreBody() {
        return """
                {
                    "name": "Test Genre"
                }
                """;
    }

    private static String getCommentBody() {
        return """
                {
                    "text": "Test comment",
                    "bookId": 1
                }
                """;
    }

    private RequestBuilder buildRequest(String method, String url, String body) {
        return switch (method) {
            case "GET" -> get(url);
            case "POST" -> post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body);
            case "PUT" -> put(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body);
            case "DELETE" -> delete(url);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }
}