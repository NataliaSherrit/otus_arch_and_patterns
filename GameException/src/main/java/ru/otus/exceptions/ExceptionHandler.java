package ru.otus.exceptions;

import ru.otus.commands.Command;

public interface ExceptionHandler {

    void handleException(Exception exception, Command command);

}
