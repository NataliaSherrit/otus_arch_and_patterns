package ru.otus;

import ru.otus.commands.QueueCommand;
import ru.otus.commands.RepeatCommand;
import ru.otus.commands.WriteToLogCommand;
import ru.otus.exceptions.RepeatExceptionHandler;
import ru.otus.exceptions.WriteToLogExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExceptionHandlersTests {
    private QueueCommand queueCommand;

    @BeforeEach
    void setUp() {
        queueCommand = mock(QueueCommand.class);
    }

    @Test
    public void testWriteToLogCommandAddedToQueue() {
        WriteToLogExceptionHandler writeToLogExceptionHandler = new WriteToLogExceptionHandler(queueCommand);
        writeToLogExceptionHandler.handleException(new Exception("Test exception"), () -> { });
        verify(queueCommand).addCommandToEndOfQueue(any(WriteToLogCommand.class));
    }

    @Test
    public void testRepeatCommandAddedToQueue() {
        RepeatExceptionHandler repeatExceptionHandler = new RepeatExceptionHandler(queueCommand);
        repeatExceptionHandler.handleException(new Exception("Test exception"), () -> { });
        verify(queueCommand).addCommandToEndOfQueue(any(RepeatCommand.class));
    }


}
