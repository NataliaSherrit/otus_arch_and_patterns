package ru.otus.game.service;
import org.springframework.stereotype.Component;
@Component
public class GameServiceImpl implements GameService {
    @Override
    public boolean receive(Message message, TokenDto metadata) {
        return true;
    }
}
