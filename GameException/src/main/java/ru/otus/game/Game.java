package ru.otus.game;

import ru.otus.commands.Command;
import ru.otus.commands.QueueCommand;
import ru.otus.exceptions.ExceptionHandler;
import ru.otus.exceptions.ExceptionHandlerStrategy;
import ru.otus.exceptions.MovingExceptionHandler;

public class Game {

    private final QueueCommand queueCommand;
    private final ExceptionHandler exceptionHandler;

    public Game(QueueCommand queueCommand, ExceptionHandlerStrategy exceptionHandlerStrategy) {
        this.queueCommand = queueCommand;
        this.exceptionHandler = new MovingExceptionHandler(queueCommand, exceptionHandlerStrategy);
    }

    public void gameProcess() {
        Command command;
        while ((command = queueCommand.readCommandFromTopOfQueue()) != null) {
            try {
                command.execute();
            } catch (Exception exception) {
                exceptionHandler.handleException(exception, command);
            }
        }
    }
}
