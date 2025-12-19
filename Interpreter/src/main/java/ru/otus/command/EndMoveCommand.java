package ru.otus.command;

import ru.otus.domain.UObject;

public class EndMoveCommand implements Command {

    private final UObject object;

    public EndMoveCommand(UObject object) {
        this.object = object;
    }

    @Override
    public void execute() {
        object.setProperty("velocity", 0);
    }
}