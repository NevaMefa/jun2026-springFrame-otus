package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("Класс CsvQuestionDao")
class CsvQuestionDaoTest {

    private TestFileNameProvider fileNameProvider;
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        fileNameProvider = Mockito.mock(TestFileNameProvider.class);
        csvQuestionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    @DisplayName("Должен корректно читать вопросы из существующего CSV файла")
    void shouldReadQuestionsFromExistingCsvFile() {
        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");

        List<Question> actualQuestions = csvQuestionDao.findAll();

        List<Question> expectedQuestions = List.of(
                new Question("Test question 1", List.of(
                        new Answer("Answer 1", true),
                        new Answer("Answer 2", false),
                        new Answer("Answer 3", false)
                )),
                new Question("Test question 2", List.of(
                        new Answer("Answer A", false),
                        new Answer("Answer B", true),
                        new Answer("Answer C", false)
                ))
        );

        assertThat(actualQuestions)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedQuestions);
    }

    @Test
    @DisplayName("Должен бросать исключение при попытке чтения несуществующего CSV файла")
    void shouldThrowExceptionWhenCsvFileNotFound() {
        String nonExistentFileName = "non-existent-file.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(nonExistentFileName);

        assertThatThrownBy(() -> csvQuestionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("Resource not found: " + nonExistentFileName);
    }
}