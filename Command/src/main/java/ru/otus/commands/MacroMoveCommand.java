package ru.otus.commands;

import java.util.ArrayList;

public class MacroMoveCommand extends MacroCommand {
    public MacroMoveCommand(MoveCommand moveCommand, QueueCommand queueCommand) {
        super(new ArrayList<>());
        commands.add(moveCommand);
        commands.add(new AddToQueueCommand(this, queueCommand));
    }
}
