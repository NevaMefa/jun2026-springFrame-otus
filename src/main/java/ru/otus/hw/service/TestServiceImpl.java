package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;
    private final QuestionDao questionDao;
    private final QuestionConverter questionConverter;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        List<Question> questions = questionDao.findAll();

        for (int i = 0; i < questions.size(); i++) {
            String questionString = questionConverter.convertQuestionToString(questions.get(i), i + 1);
            ioService.printLine(questionString);
            ioService.printLine(""); // пустая строка между вопросами
        }
    }
}