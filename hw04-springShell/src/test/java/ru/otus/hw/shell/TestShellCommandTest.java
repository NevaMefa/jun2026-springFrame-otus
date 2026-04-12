package ru.otus.hw.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.service.TestRunnerService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@DisplayName("Команда test")
class TestShellCommandTest {

    @MockBean
    private TestRunnerService testRunnerService;

    @Autowired
    private TestShellCommand testShellCommand;

    @Test
    @DisplayName("Должен вызывать TestRunnerService.run() при выполнении команды test")
    void shouldCallTestRunnerService() {
        testShellCommand.runTest();
        verify(testRunnerService, times(1)).run();
    }
}