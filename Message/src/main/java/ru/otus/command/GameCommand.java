package ru.otus.command;

import ru.otus.ioc.IoC;

public class GameCommand implements Command{
    private final String id;

    public GameCommand(String id) {
        this.id = id;
    }

    @Override
    public void execute() {
        new QueueProcessCommand(queue()).execute();
    }

    private CommandQueue queue() {
        return IoC.resolve(String.format("Games.%s.CommandQueue", id));
    }

    public class AddToGameQueueCommand implements Command {

        private final Command command;

        public AddToGameQueueCommand(Command command) {
            this.command = command;
        }

        @Override
        public void execute() {
            GameCommand.this.queue().addLast(command);
        }
    }
}
