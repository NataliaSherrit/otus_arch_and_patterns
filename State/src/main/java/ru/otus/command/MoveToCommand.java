package ru.otus.command;

import ru.otus.ioc.IoC;

import java.util.function.Function;

public class MoveToCommand implements Command {

    private final CommandQueue otherQueue;

    public MoveToCommand(CommandQueue otherQueue) {
        this.otherQueue = otherQueue;
    }

    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register","CommandQueue.MoveTo", (Function<Object[], Object>) args1 -> otherQueue)).execute();
    }
}
