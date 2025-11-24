package ru.otus.test;

import ru.otus.domain.UObject;

public class MoveCommand extends TestCommand {

    public MoveCommand(UObject obj, Object[] args) {
        super(obj, args);
    }
}
