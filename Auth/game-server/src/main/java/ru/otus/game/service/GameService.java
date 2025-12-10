package ru.otus.game.service;

public interface GameService {

    boolean receive(Message message, TokenDto metadata);
}
