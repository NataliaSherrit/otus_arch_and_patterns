package ru.otus.handler;

import ru.otus.command.CommandQueue;
import ru.otus.state.State;

public class BasicQueueHandler implements QueueHandler {

    private volatile State state;
    private final CommandQueue commandQueue;

    public BasicQueueHandler(State initialState, CommandQueue commandQueue) {
        this.state = initialState;
        this.commandQueue = commandQueue;
    }

    @Override
    public void handle() {
        State currentState;
        while ((currentState = state) != null) {
            state = currentState.handle(commandQueue);
        }
    }
}
