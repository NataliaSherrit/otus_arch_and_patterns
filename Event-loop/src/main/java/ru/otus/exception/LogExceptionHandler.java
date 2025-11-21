package ru.otus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.command.Command;

public class LogExceptionHandler implements ExceptionHandler{
    private static final Logger log = LoggerFactory.getLogger(LogExceptionHandler.class);
    @Override
    public void handle(Exception exception, Command command) {
        log.error("Exception type={} message={} was thrown while executing command={}",
                exception.getClass().getName(), exception.getMessage(), command.getClass().getName());
    }
}
