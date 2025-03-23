package ru.otus.exceptions;

import ru.otus.commands.Command;
import ru.otus.commands.QueueCommand;
import ru.otus.commands.WriteToLogCommand;

public class WriteToLogExceptionHandler implements ExceptionHandler {
    private final QueueCommand queueCommand;

    public WriteToLogExceptionHandler(QueueCommand queueCommand) {
        this.queueCommand = queueCommand;
    }

    @Override
    public void handleException(Exception exception, Command command) {
        queueCommand.addCommandToEndOfQueue(new WriteToLogCommand(exception, command));
    }
}
