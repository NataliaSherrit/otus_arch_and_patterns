package ru.otus.command;

import ru.otus.exception.CommandException;

import java.util.List;

public class MacroCommand implements Command {
    protected final List<Command> commands;

    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        try {
            commands.forEach(Command::execute);
        } catch (Exception ex) {
            throw new CommandException(ex);
        }
    }
}