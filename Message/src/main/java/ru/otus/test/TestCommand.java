package ru.otus.test;

import ru.otus.command.Command;
import ru.otus.domain.UObject;

public abstract  class TestCommand implements Command {

    public final UObject obj;
    public final Object[] args;

    TestCommand(UObject obj, Object[] args) {
        this.obj = obj;
        this.args = args;
    }

    @Override
    public void execute() {

    }
}

