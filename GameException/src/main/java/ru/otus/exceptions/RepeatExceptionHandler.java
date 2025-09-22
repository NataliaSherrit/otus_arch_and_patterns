package ru.otus.exceptions;

import ru.otus.commands.Command;
import ru.otus.commands.DoubleRepeatCommand;
import ru.otus.commands.QueueCommand;
import ru.otus.commands.RepeatCommand;

public class RepeatExceptionHandler implements ExceptionHandler {

    private final QueueCommand queueCommand;

    public RepeatExceptionHandler(QueueCommand queueCommand) {
        this.queueCommand = queueCommand;
    }

    @Override
    public void handleException(Exception exception, Command command) {
        if (command instanceof RepeatCommand) {
            queueCommand.addCommandToEndOfQueue(new DoubleRepeatCommand(command));
        } else {
            queueCommand.addCommandToEndOfQueue(new RepeatCommand(command));
        }
    }
}
