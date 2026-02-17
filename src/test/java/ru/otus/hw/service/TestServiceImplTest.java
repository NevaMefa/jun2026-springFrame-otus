package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestService testService;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void executeTest_ShouldNotThrowExceptions() {
        // Given
        List<Question> questions = Arrays.asList(
                new Question("Question 1", Arrays.asList(
                        new Answer("Answer 1-1", true),
                        new Answer("Answer 1-2", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);

        // When & Then
        assertDoesNotThrow(() -> testService.executeTest());
    }
}