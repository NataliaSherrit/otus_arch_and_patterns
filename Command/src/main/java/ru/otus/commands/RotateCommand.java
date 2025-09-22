package ru.otus.commands;


import ru.otus.domain.IRotateObject;

public class RotateCommand implements Command {
    private final IRotateObject rotateObject;

    public RotateCommand(IRotateObject rotateObj) {
        this.rotateObject = rotateObj;
    }

    public void execute() {
        rotateObject.setDirection((rotateObject.getDirection() + rotateObject.getAngular()) % rotateObject.getNumOfDirections());
    }
}
