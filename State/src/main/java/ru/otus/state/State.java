package ru.otus.state;

import ru.otus.command.CommandQueue;

public interface State {

    State handle(CommandQueue queue);
}
