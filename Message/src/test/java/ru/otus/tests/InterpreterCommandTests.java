package ru.otus.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.command.Command;
import ru.otus.command.CommandQueue;
import ru.otus.command.InterpreterCommand;
import ru.otus.domain.UObject;
import ru.otus.handler.QueueHandler;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.test.FlyCommand;
import ru.otus.test.MoveCommand;
import ru.otus.test.TestCommand;

import java.util.function.Function;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.*;

public class InterpreterCommandTests {

    private String gameId;
    private String objectId;
    private UObject object;
    private CommandQueue queue;
    private String moveId;
    private TestCommand moveCommand;
    private String flyId;
    private TestCommand flyCommand;

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        QueueHandler queueHandler = mock(QueueHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Queue.Handler", (Function<Object[], Object>) args -> queueHandler)).execute();

        moveId = "Move";
        ((Command) IoC.resolve("IoC.Register", moveId, (Function<Object[], Object>) args -> {
            moveCommand = spy(new MoveCommand((UObject) args[0], (Object[]) args[1]));
            return moveCommand;
        })).execute();
        flyId = "Fly";
        ((Command) IoC.resolve("IoC.Register", flyId, (Function<Object[], Object>) args -> {
            flyCommand = spy(new FlyCommand((UObject) args[0], (Object[]) args[1]));
            return flyCommand;
        })).execute();

        ((Command) IoC.resolve("IoC.Register", "Games.CreateQueue", (Function<Object[], Object>) args1 -> queue)).execute();

        gameId = "game";
        objectId = "123";
        object = mock(UObject.class);
        queue = mock(CommandQueue.class);
        IoC.resolve("Games.Create", gameId);
        IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object);
        IoC.resolve(String.format("Games.%s.AllowedOperations.Add", gameId), moveId);
    }

    @Test
    public void checkThatProcessHasCorrectMessageTest() {
        Object[] args = new Object[]{ "123", 1 };
        new InterpreterCommand(gameId, objectId, moveId, args).execute();
        verify(queue, times(1)).addLast(refEq(moveCommand));
        assertThat(moveCommand.obj).isSameAs(object);
        assertThat(moveCommand.args).isSameAs(args);
    }

    @Test
    public void checkThatThrowsExceptionIfObjectNotFoundTest() {
        Object[] args = new Object[]{ "123", 1 };
        assertThatThrownBy(() -> {
            new InterpreterCommand(gameId, "567", moveId, args).execute();
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Object not found");
    }

    @Test
    public void checkThatThrowsExceptionIfOperationNotAllowedTest() {
        Object[] args = new Object[]{ "123", 1 };
        assertThatThrownBy(() -> {
            new InterpreterCommand(gameId, objectId, flyId, args).execute();
        }).isInstanceOf(IllegalStateException.class).hasMessage("Operation not allowed");
    }
}
