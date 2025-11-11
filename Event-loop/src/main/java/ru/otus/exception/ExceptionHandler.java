package ru.otus.exception;

import ru.otus.command.Command;

public interface ExceptionHandler {
    void handle(Exception exception, Command command);
}
