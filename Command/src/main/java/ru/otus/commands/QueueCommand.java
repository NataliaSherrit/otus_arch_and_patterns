package ru.otus.commands;

public interface QueueCommand {
    void addLast(Command command);

    Command readFirst();
}
