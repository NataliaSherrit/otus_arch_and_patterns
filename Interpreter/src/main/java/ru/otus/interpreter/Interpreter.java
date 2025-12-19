package ru.otus.interpreter;

import ru.otus.command.Command;
import ru.otus.domain.UObject;

public interface Interpreter {

    Command interpret(UObject object);
}
