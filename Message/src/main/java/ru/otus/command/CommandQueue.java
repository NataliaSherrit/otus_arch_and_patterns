package ru.otus.command;

public interface CommandQueue {
    void addLast(Command command);
    Command readFirst();
}
