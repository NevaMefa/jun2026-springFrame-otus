package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CsvQuestionDaoIntegrationTest {

    @Test
    void findAll_ShouldReadQuestionsCorrectly() {
        TestFileNameProvider fileNameProvider = new AppProperties("test-questions.csv");
        CsvQuestionDao dao = new CsvQuestionDao(fileNameProvider);

        List<Question> questions = dao.findAll();

        assertThat(questions).hasSize(2);
        Question first = questions.get(0);
        assertThat(first.text()).isEqualTo("Test question 1");
        assertThat(first.answers()).hasSize(3);
        assertThat(first.answers().get(0).isCorrect()).isTrue();
        assertThat(first.answers().get(1).isCorrect()).isFalse();
        assertThat(first.answers().get(2).isCorrect()).isFalse();

        Question second = questions.get(1);
        assertThat(second.text()).isEqualTo("Test question 2");
        assertThat(second.answers()).hasSize(3);
        assertThat(second.answers().get(0).isCorrect()).isFalse();
        assertThat(second.answers().get(1).isCorrect()).isTrue();
        assertThat(second.answers().get(2).isCorrect()).isFalse();
    }

    @Test
    void findAll_ShouldThrowExceptionWhenFileNotFound() {
        TestFileNameProvider fileNameProvider = new AppProperties("nonexistent.csv");
        CsvQuestionDao dao = new CsvQuestionDao(fileNameProvider);

        assertThatThrownBy(dao::findAll)
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("Error reading questions from file: nonexistent.csv");
    }
}