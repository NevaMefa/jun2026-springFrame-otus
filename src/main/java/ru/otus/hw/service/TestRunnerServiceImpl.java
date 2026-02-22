package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.QuestionReadException;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;
    private final IOService ioService;

    @Override
    public void run() {
        try {
            testService.executeTest();
        } catch (QuestionReadException e) {
            ioService.printLine("Error: Failed to load questions. Please check that the questions file exists and is properly formatted.");
        } catch (Exception e) {
            ioService.printLine("Error: An unexpected error occurred while running the test.");
        }
    }
}