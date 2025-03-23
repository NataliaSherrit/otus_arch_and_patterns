package ru.otus.commands;

import java.util.Deque;
import java.util.LinkedList;

public class QueueCommandImplementation implements QueueCommand {

    private final Deque<Command> queue;

    public QueueCommandImplementation() {
        this.queue = new LinkedList<>();
    }
    @Override
    public void addCommandToEndOfQueue(Command object) {
        queue.addLast(object);
    }

    @Override
    public Command readCommandFromTopOfQueue() {
        return queue.pollFirst();
    }
}
