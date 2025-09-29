package ru.otus.test;

public abstract class AbstractMovingObject implements IMovingObject {
    private final String name;
    private int pos;

    AbstractMovingObject(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(int pos) {
        this.pos = pos;
    }

    @Override
    public int getPosition() {
        return pos;
    }

    @Override
    public String getName() {
        return name;
    }
}
