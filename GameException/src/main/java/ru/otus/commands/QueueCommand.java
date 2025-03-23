package ru.otus.commands;

public interface QueueCommand {

        void addCommandToEndOfQueue(Command command);

        Command readCommandFromTopOfQueue();

}
