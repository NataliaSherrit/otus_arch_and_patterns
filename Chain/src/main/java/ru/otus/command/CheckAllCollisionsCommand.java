package ru.otus.command;

import ru.otus.domain.GameMap;
import ru.otus.ioc.IoC;

public class CheckAllCollisionsCommand implements Command {

    public CheckAllCollisionsCommand() {
    }

    @Override
    public void execute() {
        GameMap gameMap = IoC.resolve("GameMap");
        gameMap.handleCollisions();
    }
}
