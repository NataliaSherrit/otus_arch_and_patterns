package ru.otus.commands;

public class AddToQueueCommand implements Command {

    private final Command command;
    private final QueueCommand queueCommand;

    public AddToQueueCommand(Command command, QueueCommand queueCommand) {
        this.command = command;
        this.queueCommand = queueCommand;
    }

    @Override
    public void execute() {
        queueCommand.addLast(command);
    }
}
