package ru.otus.exceptions;
import ru.otus.commands.Command;

public interface ExceptionHandlerStrategy {

    String getExceptionHandler(Exception exception, Command command);
}
