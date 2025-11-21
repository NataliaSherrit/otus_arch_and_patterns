package ru.otus.command;

import ru.otus.ioc.IoC;

import java.util.function.Function;

public class HardStopCommand implements Command {
    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register","CommandQueue.NextCommand", (Function<Object[], Object>) args1 -> (Command) null)).execute();
    }
}
