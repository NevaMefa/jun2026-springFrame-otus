package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.IOService;
import ru.otus.hw.InputService;
import ru.otus.hw.QuestionConverter;
import ru.otus.hw.TestService;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final QuestionConverter questionConverter;

    private final InputService inputService;

    private final int rightAnswersCount;

    private record UserData(String firstName, String lastName) {}

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        UserData user = readUserData();
        List<Question> questions = questionDao.findAll();

        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (processQuestion(questions.get(i), i + 1)) {
                correctAnswers++;
            }
        }

        printResult(user.firstName(), user.lastName(), correctAnswers, questions.size());
    }

    private UserData readUserData() {
        ioService.printLine("Enter your first name:");
        String firstName = inputService.readString();
        ioService.printLine("Enter your last name:");
        String lastName = inputService.readString();
        return new UserData(firstName, lastName);
    }

    private boolean processQuestion(Question question, int questionNumber) {
        ioService.printLine(questionConverter.convertQuestionToString(question, questionNumber));
        ioService.printLine("Your answer (enter number):");
        int userAnswerIndex = inputService.readInt();

        List<Answer> answers = question.answers();
        if (userAnswerIndex >= 1 && userAnswerIndex <= answers.size()) {
            Answer selectedAnswer = answers.get(userAnswerIndex - 1);
            ioService.printLine("");
            return selectedAnswer.isCorrect();
        } else {
            ioService.printLine("Invalid answer number. Please enter a number between 1 and " + answers.size());
            ioService.printLine("");
            return false;
        }
    }

    private void printResult(String firstName, String lastName, int correctAnswers, int totalQuestions) {
        ioService.printLine("Student: " + firstName + " " + lastName);
        ioService.printFormattedLine("You answered correctly %d out of %d questions.", correctAnswers, totalQuestions);
        if (correctAnswers >= rightAnswersCount) {
            ioService.printLine("Congratulations! You passed the test.");
        } else {
            ioService.printFormattedLine("Sorry, you need at least %d correct answers to pass.", rightAnswersCount);
        }
    }
}