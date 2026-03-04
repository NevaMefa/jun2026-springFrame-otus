package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.IOService;
import ru.otus.hw.InputService;
import ru.otus.hw.QuestionConverter;
import ru.otus.hw.StreamsIOService;
import ru.otus.hw.StreamsInputService;
import ru.otus.hw.TestRunnerService;
import ru.otus.hw.TestRunnerServiceImpl;
import ru.otus.hw.TestService;
import ru.otus.hw.service.TestServiceImpl;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public TestFileNameProvider testFileNameProvider(@Value("${questions.file}") String fileName) {
        return new AppProperties(fileName);
    }

    @Bean
    public QuestionDao questionDao(TestFileNameProvider fileNameProvider) {
        return new CsvQuestionDao(fileNameProvider);
    }

    @Bean
    public IOService ioService() {
        return new StreamsIOService(System.out);
    }

    @Bean
    public InputService inputService() {
        return new StreamsInputService(System.in);
    }

    @Bean
    public QuestionConverter questionConverter() {
        return new QuestionConverter();
    }

    @Bean
    public TestService testService(IOService ioService, QuestionDao questionDao,
                                   QuestionConverter questionConverter, InputService inputService,
                                   @Value("${right.answers.count}") int rightAnswersCount) {
        return new TestServiceImpl(ioService, questionDao, questionConverter, inputService, rightAnswersCount);
    }

    @Bean
    public TestRunnerService testRunnerService(TestService testService, IOService ioService) {
        return new TestRunnerServiceImpl(testService, ioService);
    }
}