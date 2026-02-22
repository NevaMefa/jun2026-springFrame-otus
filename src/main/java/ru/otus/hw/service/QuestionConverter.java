package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class QuestionConverter {

    public String convertQuestionToString(Question question, int questionNumber) {
        StringBuilder sb = new StringBuilder();

        // Добавляем вопрос
        sb.append(String.format("Question %d: %s%n", questionNumber, question.text()));

        // Добавляем варианты ответов
        List<Answer> answers = question.answers();
        String answersString = IntStream.range(0, answers.size())
                .mapToObj(j -> String.format("  %d) %s", j + 1, answers.get(j).text()))
                .collect(Collectors.joining("%n"));

        sb.append(answersString);

        return sb.toString();
    }

    public List<String> convertQuestionsToStrings(List<Question> questions) {
        return IntStream.range(0, questions.size())
                .mapToObj(i -> convertQuestionToString(questions.get(i), i + 1))
                .collect(Collectors.toList());
    }
}