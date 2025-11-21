package ru.otus.handler;

import ru.otus.command.CommandQueue;

public interface QueueHandler {
    void handle(CommandQueue queue);
}
