package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class TestShellCommand {

    private final TestRunnerService testRunnerService;

    @ShellMethod(key = "test", value = "Run the student test")
    public void runTest() {
        testRunnerService.run();
    }
}