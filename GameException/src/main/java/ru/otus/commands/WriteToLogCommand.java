package ru.otus.commands;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteToLogCommand implements Command{

    private final Exception exception;
    private final Command command;

    public WriteToLogCommand(Exception exception, Command command) {
        this.exception = exception;
        this.command = command;
    }

    @Override
    public void execute() {
        log.error("Exception {} with message {} was thrown while executing command {}",
                exception.getClass().getName(), exception.getMessage(), command.getClass().getName());
    }
}
