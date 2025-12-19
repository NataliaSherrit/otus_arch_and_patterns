package ru.otus.command;

import ru.otus.domain.UObject;
import ru.otus.ioc.IoC;

public class InterpretCommand implements Command {

    private final String gameId;
    private final UObject order;

    public InterpretCommand(String gameId, UObject order) {
        this.gameId = gameId;
        this.order = order;
    }
    @Override
    public void execute() {
        Command command = IoC.resolve("Interpreter.Command.Execute", gameId, order);
        command.execute();
    }

}
