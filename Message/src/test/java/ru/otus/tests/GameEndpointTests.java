package ru.otus.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.command.Command;
import ru.otus.command.CommandQueue;
import ru.otus.command.InterpreterCommand;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.message.GameEndpoint;
import ru.otus.message.Message;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GameEndpointTests {
    private CommandQueue queue;
    private String gameId;
    private GameEndpoint gameEndpoint;

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        queue = mock(CommandQueue.class);
        ((Command) IoC.resolve("IoC.Register", "Games.CreateQueue", (Function<Object[], Object>) args1 -> {
            if (gameId.equals(args1[0])) {
                return queue;
            } else {
                return null;
            }
        })).execute();

        gameId = "game1";
        gameEndpoint = new GameEndpoint();
    }

    @Test
    public void checkThatProcessHasCorrectMessageTest() {
        IoC.resolve("Games.Create", gameId);
        IoC.resolve("Games.Create", "game2");
        IoC.resolve("Games.Create", "game3");

        String objectId = "123";
        String operationId = "Move";
        Object[] args = new Object[]{ "123", 1 };
        gameEndpoint.receiveMessage(new Message.Builder()
                .setGameId(gameId)
                .setObjectId(objectId)
                .setOperationId(operationId)
                .setArgs(args)
                .build()
        );

        verify(queue, times(1)).addLast(argThat(command -> {
            assertThat(command)
                    .isInstanceOf(InterpreterCommand.class);
            return true;
        }));
    }

}
