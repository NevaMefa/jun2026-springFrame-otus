package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private QuestionConverter questionConverter;

    private TestService testService;

    @BeforeEach
    void setUp() {
        testService = new TestServiceImpl(ioService, questionDao, questionConverter);
    }

    @Test
    void executeTest_ShouldNotThrowExceptions() {
        List<Question> questions = Arrays.asList(
                new Question("Question 1", Arrays.asList(
                        new Answer("Answer 1-1", true),
                        new Answer("Answer 1-2", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(questionConverter.convertQuestionToString(any(Question.class), anyInt()))
                .thenReturn("Question 1: Question 1\n  1) Answer 1-1\n  2) Answer 1-2");

        assertDoesNotThrow(() -> testService.executeTest());
    }
}