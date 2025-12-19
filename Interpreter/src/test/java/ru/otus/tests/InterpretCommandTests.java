package ru.otus.tests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.command.Command;
import ru.otus.command.InterpretCommand;
import ru.otus.domain.UObject;
import ru.otus.interpreter.Interpreter;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;

import java.util.function.Function;

import static org.mockito.Mockito.*;
class InterpretCommandTests {

    private String gameId;
    private Interpreter interpreter;

    @BeforeEach
    void setUp() {

        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        gameId = "game";
        IoC.resolve("Games.Create", gameId);

        interpreter = mock(Interpreter.class);
        ((Command) IoC.resolve("IoC.Register", "Interpreter.Command.Execute", (Function<Object[], Object>) args -> {
            if (gameId.equals(args[0])) {
                return interpreter.interpret((UObject) args[1]);
            } else {
                return null;
            }
        })).execute();
    }

    @Test
    public void callInterpreterAndAddCommandToQueueTest() {
        UObject order = mock(UObject.class);
        Command command = mock(Command.class);
        when(interpreter.interpret(refEq(order))).thenReturn(command);
        new InterpretCommand(gameId, order).execute();
        verify(command, times(1)).execute();
    }

}
