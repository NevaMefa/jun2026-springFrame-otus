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

        List<Question> questions = csvQuestionDao.findAll();

        assertThat(questions).isNotNull().hasSize(6);

        Question q1 = questions.get(0);
        assertThat(q1.text()).isEqualTo("Is there life on Mars?");
        List<Answer> a1 = q1.answers();
        assertThat(a1).hasSize(3);
        assertThat(a1.get(0).text()).isEqualTo("Science doesn't know this yet");
        assertThat(a1.get(0).isCorrect()).isTrue();
        assertThat(a1.get(1).text()).isEqualTo("Certainly. The red UFO is from Mars. And green is from Venus");
        assertThat(a1.get(1).isCorrect()).isFalse();
        assertThat(a1.get(2).text()).isEqualTo("Absolutely not");
        assertThat(a1.get(2).isCorrect()).isFalse();

        Question q2 = questions.get(1);
        assertThat(q2.text()).isEqualTo("How should resources be loaded form jar in Java?");
        List<Answer> a2 = q2.answers();
        assertThat(a2).hasSize(3);
        assertThat(a2.get(0).text()).isEqualTo("ClassLoader#geResourceAsStream or ClassPathResource#getInputStream");
        assertThat(a2.get(0).isCorrect()).isTrue();
        assertThat(a2.get(1).text()).isEqualTo("ClassLoader#geResource#getFile + FileReader");
        assertThat(a2.get(1).isCorrect()).isFalse();
        assertThat(a2.get(2).text()).isEqualTo("Wingardium Leviosa");
        assertThat(a2.get(2).isCorrect()).isFalse();

        Question q3 = questions.get(2);
        assertThat(q3.text()).isEqualTo("Which option is a good way to handle the exception?");
        List<Answer> a3 = q3.answers();
        assertThat(a3).hasSize(4);
        assertThat(a3.get(0).text()).isEqualTo("@SneakyThrow");
        assertThat(a3.get(0).isCorrect()).isFalse();
        assertThat(a3.get(1).text()).isEqualTo("e.printStackTrace()");
        assertThat(a3.get(1).isCorrect()).isFalse();
        assertThat(a3.get(2).text()).isEqualTo("Rethrow with wrapping in business exception (for example, QuestionReadException)");
        assertThat(a3.get(2).isCorrect()).isTrue();
        assertThat(a3.get(3).text()).isEqualTo("Ignoring exception");
        assertThat(a3.get(3).isCorrect()).isFalse();

        Question q4 = questions.get(3);
        assertThat(q4.text()).isEqualTo("What is the capital of France?");
        List<Answer> a4 = q4.answers();
        assertThat(a4).hasSize(4);
        assertThat(a4.get(0).text()).isEqualTo("Paris");
        assertThat(a4.get(0).isCorrect()).isTrue();
        assertThat(a4.get(1).text()).isEqualTo("London");
        assertThat(a4.get(1).isCorrect()).isFalse();
        assertThat(a4.get(2).text()).isEqualTo("Berlin");
        assertThat(a4.get(2).isCorrect()).isFalse();
        assertThat(a4.get(3).text()).isEqualTo("Madrid");
        assertThat(a4.get(3).isCorrect()).isFalse();

        Question q5 = questions.get(4);
        assertThat(q5.text()).isEqualTo("Which data structure uses LIFO (Last In First Out) principle?");
        List<Answer> a5 = q5.answers();
        assertThat(a5).hasSize(4);
        assertThat(a5.get(0).text()).isEqualTo("Stack");
        assertThat(a5.get(0).isCorrect()).isTrue();
        assertThat(a5.get(1).text()).isEqualTo("Queue");
        assertThat(a5.get(1).isCorrect()).isFalse();
        assertThat(a5.get(2).text()).isEqualTo("Array");
        assertThat(a5.get(2).isCorrect()).isFalse();
        assertThat(a5.get(3).text()).isEqualTo("Linked List");
        assertThat(a5.get(3).isCorrect()).isFalse();

        Question q6 = questions.get(5);
        assertThat(q6.text()).isEqualTo("What does SOLID stand for in object-oriented design?");
        List<Answer> a6 = q6.answers();
        assertThat(a6).hasSize(4);
        assertThat(a6.get(0).text()).isEqualTo("Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion");
        assertThat(a6.get(0).isCorrect()).isTrue();
        assertThat(a6.get(1).text()).isEqualTo("Structured Object-Layered Interface Design");
        assertThat(a6.get(1).isCorrect()).isFalse();
        assertThat(a6.get(2).text()).isEqualTo("Simple Object Linking and Inclusion Directory");
        assertThat(a6.get(2).isCorrect()).isFalse();
        assertThat(a6.get(3).text()).isEqualTo("System Oriented Language for Interface Development");
        assertThat(a6.get(3).isCorrect()).isFalse();
    }

    @Test
    @DisplayName("Должен бросать исключение при попытке чтения несуществующего CSV файла")
    void shouldThrowExceptionWhenCsvFileNotFound() {
        String nonExistentFileName = "non-existent-file.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(nonExistentFileName);

        assertThatThrownBy(() -> csvQuestionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("File not found: " + nonExistentFileName);
    }
}
