package ru.otus;

public class Rotate {
    private final IRotateObject rotateObject;

    public Rotate(IRotateObject rotateObj) {
        this.rotateObject = rotateObj;
    }

    public void execute() {
        rotateObject.setDirection((rotateObject.getDirection() + rotateObject.getAngular()) % rotateObject.getNumOfDirections());
    }
}
