package ru.otus.exceptions;

import ru.otus.commands.Command;
import ru.otus.commands.DoubleRepeatCommand;

public class DoubleRepeatThenWriteToLogExceptionHandlerStrategy implements ExceptionHandlerStrategy {
    @Override
    public String getExceptionHandler(Exception exception, Command command) {
        if (command instanceof DoubleRepeatCommand) {
            return WriteToLogExceptionHandler.class.getName();
        } else {
            return RepeatExceptionHandler.class.getName();
        }
    }
}
