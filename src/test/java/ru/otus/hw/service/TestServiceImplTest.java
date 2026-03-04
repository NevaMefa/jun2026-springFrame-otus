package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.*;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private QuestionConverter questionConverter;

    @Mock
    private InputService inputService;

    private TestService testService;

    @BeforeEach
    void setUp() {
        testService = new ru.otus.hw.service.TestServiceImpl(ioService, questionDao, questionConverter, inputService, 1);
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
        when(inputService.readString())
                .thenReturn("John")
                .thenReturn("Doe");
        when(inputService.readInt()).thenReturn(1);

        assertDoesNotThrow(() -> testService.executeTest());

        verify(ioService, atLeastOnce()).printLine(anyString());
        verify(questionDao, times(1)).findAll();
        verify(inputService, times(2)).readString();
        verify(inputService, times(1)).readInt();
    }
}