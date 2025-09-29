package ru.otus.test;

import ru.otus.command.Command;

public class MoveCommand implements Command {
    public final IMovingObject iMovingObject;

    public MoveCommand(IMovingObject iMovingObject) {
        this.iMovingObject = iMovingObject;
    }

    @Override
    public void execute() {
        iMovingObject.setPosition(iMovingObject.getPosition() + 1);
    }
}
