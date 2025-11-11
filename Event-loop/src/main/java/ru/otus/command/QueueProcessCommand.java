package ru.otus.command;

import ru.otus.ioc.IoC;
import ru.otus.queue.QueueHandler;

public class QueueProcessCommand implements Command{
    @Override
    public void execute() {
        ((QueueHandler) IoC.resolve("Queue.Handler")).handle();
    }
}
