package ru.otus.command;

import ru.otus.domain.UObject;

public class FireCommand implements Command {

    private final UObject object;
    private final int fireDirection;

    public FireCommand(UObject object, int fireDirection) {
        this.object = object;
        this.fireDirection = fireDirection;
    }

    @Override
    public void execute() {
        object.setProperty("fireDirection", fireDirection);
    }
}
