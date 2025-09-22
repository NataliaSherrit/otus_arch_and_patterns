package ru.otus.exceptions;

import ru.otus.commands.Command;
import ru.otus.commands.RepeatCommand;

public class RepeatThenWriteToLogExceptionHandlerStrategy implements ExceptionHandlerStrategy{
    @Override
    public String getExceptionHandler(Exception exception, Command command) {
        if (command instanceof RepeatCommand ) {
            return WriteToLogExceptionHandler.class.getName();
        }
        else {
            return RepeatExceptionHandler.class.getName();
        }
    }
}
