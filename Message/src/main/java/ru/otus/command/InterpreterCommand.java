package ru.otus.command;

import ru.otus.domain.UObject;
import ru.otus.ioc.IoC;

public class InterpreterCommand implements Command{
    private final String gameId;
    private final String objectId;
    private final String operationId;
    private final Object[] args;

    public InterpreterCommand(String gameId, String objectId, String operationId, Object[] args) {
        this.gameId = gameId;
        this.objectId = objectId;
        this.operationId = operationId;
        this.args = args;
    }
    @Override
    public void execute() {
        UObject object = IoC.resolve(String.format("Games.%s.Objects.Get", gameId), objectId);
        if (object == null) {
            throw new IllegalArgumentException("Object not found");
        }
        boolean isAllowed = IoC.resolve(String.format("Games.%s.AllowedOperations.Get", gameId), operationId);
        if (!isAllowed) {
            throw new IllegalStateException("Operation not allowed");
        }
        Command command = IoC.resolve(operationId, object, args);
        CommandQueue commandQueue = IoC.resolve(String.format("Games.%s.CommandQueue", gameId));
        commandQueue.addLast(command);
    }
}
