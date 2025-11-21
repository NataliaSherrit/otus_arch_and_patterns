package ru.otus.queue;

import ru.otus.command.Command;
import ru.otus.exception.ExceptionHandler;
import ru.otus.ioc.IoC;

public class BasicQueueHandler implements QueueHandler{
    @Override
    public void handle() {
        Command command;
        while ((command = IoC.resolve("CommandQueue.NextCommand")) != null) {
            try {
                command.execute();
            } catch (Exception exception) {
                ((ExceptionHandler) IoC.resolve("Exception.Handler")).handle(exception, command);
            }
        }
    }
}
