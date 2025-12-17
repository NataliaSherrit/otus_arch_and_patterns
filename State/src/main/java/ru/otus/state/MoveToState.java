package ru.otus.state;

import ru.otus.command.Command;
import ru.otus.command.CommandQueue;
import ru.otus.command.HardStopCommand;
import ru.otus.command.RunCommand;

public class MoveToState implements State {

    private final CommandQueue otherQueue;

    public MoveToState(CommandQueue otherQueue) {
        this.otherQueue = otherQueue;
    }

    @Override
    public State handle(CommandQueue queue) {
        Command command = queue.readFirst();
        State nextState = this;
        if (command != null) {
            // команды HardStop и Run не нужно добавлять в другую очередь
            if (command instanceof HardStopCommand) {
                nextState = null;
            } else if (command instanceof RunCommand) {
                nextState = new DefaultState();
            } else {
                otherQueue.addLast(command);
            }
        }
        return nextState;
    }
}
