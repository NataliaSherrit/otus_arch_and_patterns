package ru.otus.exceptions;

import ru.otus.commands.Command;
import ru.otus.commands.QueueCommand;

import java.util.HashMap;
import java.util.Map;

public class MovingExceptionHandler implements ExceptionHandler{

    private final Map<String, ExceptionHandler> exceptionHandlers;
    private final ExceptionHandlerStrategy exceptionHandlerStrategy;
    public MovingExceptionHandler(QueueCommand queueCommand, ExceptionHandlerStrategy exceptionHandlerStrategy) {
        this.exceptionHandlerStrategy = exceptionHandlerStrategy;
        this.exceptionHandlers = new HashMap<>();
        this.exceptionHandlers.put(WriteToLogExceptionHandler.class.getName(), new WriteToLogExceptionHandler(queueCommand));
        this.exceptionHandlers.put(RepeatExceptionHandler.class.getName(), new RepeatExceptionHandler(queueCommand));
    }

    @Override
    public void handleException(Exception exception, Command command) {
        ExceptionHandler exceptionHandler = exceptionHandlers.get(exceptionHandlerStrategy.getExceptionHandler(exception, command));
        if (exceptionHandler == null) {
            throw new NoSuchHandlerException(exception);
        }
        exceptionHandler.handleException(exception, command);
    }
}
