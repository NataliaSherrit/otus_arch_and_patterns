package ru.otus.message;

import ru.otus.command.GameCommand;
import ru.otus.ioc.IoC;

public class GameEndpoint implements Endpoint{
    public void receiveMessage(Message message) {
        GameCommand game = IoC.resolve("Games.GetById", message.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }
        game.new AddToGameQueueCommand(IoC.resolve("InterpretCommand", message.getGameId(), message.getObjectId(), message.getOperationId(), message.getArgs())).execute();
    }
}
