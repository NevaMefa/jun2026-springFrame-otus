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

        Question firstQuestion = questions.get(0);
        assertThat(firstQuestion.text()).isEqualTo("Is there life on Mars?");
        List<Answer> firstAnswers = firstQuestion.answers();
        assertThat(firstAnswers).hasSize(3);
        assertThat(firstAnswers.get(0).text()).isEqualTo("Science doesn't know this yet");
        assertThat(firstAnswers.get(0).isCorrect()).isTrue();
        assertThat(firstAnswers.get(1).text()).isEqualTo("Certainly. The red UFO is from Mars. And green is from Venus");
        assertThat(firstAnswers.get(1).isCorrect()).isFalse();
        assertThat(firstAnswers.get(2).text()).isEqualTo("Absolutely not");
        assertThat(firstAnswers.get(2).isCorrect()).isFalse();

        Question secondQuestion = questions.get(1);
        assertThat(secondQuestion.text()).isEqualTo("How should resources be loaded form jar in Java?");
        List<Answer> secondAnswers = secondQuestion.answers();
        assertThat(secondAnswers).hasSize(3);
        assertThat(secondAnswers.get(0).text())
                .isEqualTo("ClassLoader#geResourceAsStream or ClassPathResource#getInputStream");
        assertThat(secondAnswers.get(0).isCorrect()).isTrue();
        assertThat(secondAnswers.get(1).text()).isEqualTo("ClassLoader#geResource#getFile + FileReader");
        assertThat(secondAnswers.get(1).isCorrect()).isFalse();
        assertThat(secondAnswers.get(2).text()).isEqualTo("Wingardium Leviosa");
        assertThat(secondAnswers.get(2).isCorrect()).isFalse();

        Question thirdQuestion = questions.get(2);
        assertThat(thirdQuestion.text()).isEqualTo("Which option is a good way to handle the exception?");
        List<Answer> thirdAnswers = thirdQuestion.answers();
        assertThat(thirdAnswers).hasSize(4);
        assertThat(thirdAnswers.get(0).text()).isEqualTo("@SneakyThrow");
        assertThat(thirdAnswers.get(0).isCorrect()).isFalse();
        assertThat(thirdAnswers.get(1).text()).isEqualTo("e.printStackTrace()");
        assertThat(thirdAnswers.get(1).isCorrect()).isFalse();
        assertThat(thirdAnswers.get(2).text())
                .isEqualTo("Rethrow with wrapping in business exception (for example, QuestionReadException)");
        assertThat(thirdAnswers.get(2).isCorrect()).isTrue();
        assertThat(thirdAnswers.get(3).text()).isEqualTo("Ignoring exception");
        assertThat(thirdAnswers.get(3).isCorrect()).isFalse();

        Question fourthQuestion = questions.get(3);
        assertThat(fourthQuestion.text()).isEqualTo("What is the capital of France?");
        List<Answer> fourthAnswers = fourthQuestion.answers();
        assertThat(fourthAnswers).hasSize(4);
        assertThat(fourthAnswers.get(0).text()).isEqualTo("Paris");
        assertThat(fourthAnswers.get(0).isCorrect()).isTrue();
        assertThat(fourthAnswers.get(1).text()).isEqualTo("London");
        assertThat(fourthAnswers.get(1).isCorrect()).isFalse();
        assertThat(fourthAnswers.get(2).text()).isEqualTo("Berlin");
        assertThat(fourthAnswers.get(2).isCorrect()).isFalse();
        assertThat(fourthAnswers.get(3).text()).isEqualTo("Madrid");
        assertThat(fourthAnswers.get(3).isCorrect()).isFalse();

        Question fifthQuestion = questions.get(4);
        assertThat(fifthQuestion.text()).isEqualTo("Which data structure uses LIFO (Last In First Out) principle?");
        List<Answer> fifthAnswers = fifthQuestion.answers();
        assertThat(fifthAnswers).hasSize(4);
        assertThat(fifthAnswers.get(0).text()).isEqualTo("Stack");
        assertThat(fifthAnswers.get(0).isCorrect()).isTrue();
        assertThat(fifthAnswers.get(1).text()).isEqualTo("Queue");
        assertThat(fifthAnswers.get(1).isCorrect()).isFalse();
        assertThat(fifthAnswers.get(2).text()).isEqualTo("Array");
        assertThat(fifthAnswers.get(2).isCorrect()).isFalse();
        assertThat(fifthAnswers.get(3).text()).isEqualTo("Linked List");
        assertThat(fifthAnswers.get(3).isCorrect()).isFalse();

        Question sixthQuestion = questions.get(5);
        assertThat(sixthQuestion.text()).isEqualTo("What does SOLID stand for in object-oriented design?");
        List<Answer> sixthAnswers = sixthQuestion.answers();
        assertThat(sixthAnswers).hasSize(4);
        assertThat(sixthAnswers.get(0).text())
                .isEqualTo("Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion");
        assertThat(sixthAnswers.get(0).isCorrect()).isTrue();
        assertThat(sixthAnswers.get(1).text()).isEqualTo("Structured Object-Layered Interface Design");
        assertThat(sixthAnswers.get(1).isCorrect()).isFalse();
        assertThat(sixthAnswers.get(2).text()).isEqualTo("Simple Object Linking and Inclusion Directory");
        assertThat(sixthAnswers.get(2).isCorrect()).isFalse();
        assertThat(sixthAnswers.get(3).text()).isEqualTo("System Oriented Language for Interface Development");
        assertThat(sixthAnswers.get(3).isCorrect()).isFalse();
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