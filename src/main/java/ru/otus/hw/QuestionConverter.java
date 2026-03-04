package ru.otus.hw;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionConverter {

    public String convertQuestionToString(Question question, int questionNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Question %d: %s%n", questionNumber, question.text()));

        List<Answer> answers = question.answers();
        for (int j = 0; j < answers.size(); j++) {
            sb.append(String.format("  %d) %s%n", j + 1, answers.get(j).text()));
        }

        return sb.toString();
    }

    public List<String> convertQuestionsToStrings(List<Question> questions) {
        return IntStream.range(0, questions.size())
                .mapToObj(i -> convertQuestionToString(questions.get(i), i + 1))
                .collect(Collectors.toList());
    }
}