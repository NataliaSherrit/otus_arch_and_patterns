package ru.otus.command;

import ru.otus.handler.QueueHandler;
import ru.otus.ioc.IoC;

public class QueueProcessCommand implements Command{
    private final CommandQueue queue;

    public QueueProcessCommand(CommandQueue queue) {
        this.queue = queue;
    }
    @Override
    public void execute() {
        ((QueueHandler) IoC.resolve("Queue.Handler")).handle(queue);
    }
}
