package ru.otus.exceptions;

public class CommandException extends RuntimeException {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(Exception e) {
        super(e);
    }
}