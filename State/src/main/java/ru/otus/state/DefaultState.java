package ru.otus.state;

import ru.otus.command.Command;
import ru.otus.command.CommandQueue;
import ru.otus.command.HardStopCommand;
import ru.otus.command.MoveToCommand;
import ru.otus.handler.ExceptionHandler;
import ru.otus.ioc.IoC;

public class DefaultState implements State {

    @Override
    public State handle(CommandQueue queue) {
        Command command = queue.readFirst();
        State nextState = this;
        if (command != null) {
            try {
                command.execute();
                if (command instanceof HardStopCommand) {
                    nextState = null;
                } else if (command instanceof MoveToCommand) {
                    nextState = new MoveToState(IoC.resolve("CommandQueue.MoveTo"));
                }
            } catch (Exception exception) {
                ((ExceptionHandler) IoC.resolve("Exception.Handler")).handle(exception, command);
            }
        }
        return nextState;
    }
}
