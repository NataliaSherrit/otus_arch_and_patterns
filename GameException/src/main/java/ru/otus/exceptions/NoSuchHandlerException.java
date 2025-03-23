package ru.otus.exceptions;

public class NoSuchHandlerException extends IllegalArgumentException {

    public NoSuchHandlerException(Exception e) {
        super(e);
    }

}

