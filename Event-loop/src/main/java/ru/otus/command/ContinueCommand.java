package ru.otus.command;

import ru.otus.ioc.IoC;

import java.util.function.Function;

public class ContinueCommand implements Command {
    @Override
    public void execute() {
        ((Command) IoC.resolve("IoC.Register", "CommandQueue.NextCommand", (Function<Object[], Object>) args1 -> {
            Command command = ((CommandQueue) IoC.resolve("CommandQueue")).readFirst();
            return (command == null) ? IoC.resolve("CommandQueue.EmptyCommand") : command;
        })).execute();
    }
}
