package ru.otus.tests;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.command.BeginMoveCommand;
import ru.otus.command.Command;
import ru.otus.command.EndMoveCommand;
import ru.otus.command.FireCommand;
import ru.otus.domain.UObject;
import ru.otus.exception.InterpretException;
import ru.otus.interpreter.CommandInterpreter;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
class CommandInterpreterTest {

    private final String gameId1 = "game1";
    private final String beginMoveKey = "beginMove";
    private final String endMoveKey = "endMove";
    private final String fireKey = "fire";
    private final String otherKey = "other";

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        IoC.resolve("Games.Create", gameId1);

        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), beginMoveKey, BeginMoveCommand.class);
        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), endMoveKey, EndMoveCommand.class);
        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), fireKey, FireCommand.class);
        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), otherKey, OtherCommand.class);

        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), beginMoveKey, (Function<Object[], Command>) args -> new BeginMoveCommand((UObject) args[0], (Double) args[1]));
        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), endMoveKey, (Function<Object[], Command>) args -> new EndMoveCommand((UObject) args[0]));
        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), fireKey, (Function<Object[], Command>) args -> new FireCommand((UObject) args[0], (Integer) args[1]));
        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), otherKey, (Function<Object[], Command>) args -> new OtherCommand((Integer) args[0], (String) args[1]));
    }

    @Test
    void processObjectOrderTest() throws InterruptedException {
        String username = "User1";
        String objectId1 = "object1";
        UObject object1 = mock(UObject.class);
        Map<String, Map<String, UObject>> objectsInGames = Map.of(gameId1, Map.of(objectId1, object1));

        UObject order = mock(UObject.class);
        double velocity = 1234.56;
        when(order.getProperty(eq("action"))).thenReturn(beginMoveKey);
        when(order.getProperty(eq("id"))).thenReturn(objectId1);
        when(order.getProperty(eq("initialVelocity"))).thenReturn(velocity);

        AtomicReference<Command> command = new AtomicReference<>();

        Thread thread = new Thread(() -> {
            initUserScope(username, objectsInGames);
            command.set(new CommandInterpreter(gameId1).interpret(order));
        });
        thread.start();
        thread.join();

        assertThat(command.get()).isNotNull().isInstanceOf(BeginMoveCommand.class);
        command.get().execute();
        verify(object1, times(1)).setProperty(eq("initialVelocity"), argThat(value -> Math.abs(velocity - (double) value) < 1e-3));
    }
    @Test
    void processObjectOrdersByUsersTest() throws InterruptedException {
        String username1 = "User1";
        String objectId1 = "object1";
        UObject object1 = mock(UObject.class);
        Map<String, Map<String, UObject>> objectsInGames1 = Map.of(gameId1, Map.of(objectId1, object1));
        String username2 = "User2";
        String objectId2 = "object2";
        UObject object2 = mock(UObject.class);
        Map<String, Map<String, UObject>> objectsInGames2 = Map.of(gameId1, Map.of(objectId2, object2));

        UObject order1 = mock(UObject.class);
        when(order1.getProperty(eq("action"))).thenReturn(endMoveKey);
        when(order1.getProperty(eq("id"))).thenReturn(objectId1);

        UObject order2 = mock(UObject.class);
        int fireDirection = 123;
        when(order2.getProperty(eq("action"))).thenReturn(fireKey);
        when(order2.getProperty(eq("id"))).thenReturn(objectId2);
        when(order2.getProperty(eq("fireDirection"))).thenReturn(fireDirection);

        AtomicReference<Command> command1 = new AtomicReference<>();
        AtomicReference<Command> command2 = new AtomicReference<>();

        Thread thread1 = new Thread(() -> {
            initUserScope(username1, objectsInGames1);
            command1.set(new CommandInterpreter(gameId1).interpret(order1));
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            initUserScope(username2, objectsInGames2);
            command2.set(new CommandInterpreter(gameId1).interpret(order2));
        });
        thread2.start();

        thread1.join();
        thread2.join();

        assertThat(command1.get()).isNotNull().isInstanceOf(EndMoveCommand.class);
        command1.get().execute();
        verify(object1, times(1)).setProperty(eq("velocity"), eq(0));

        assertThat(command2.get()).isNotNull().isInstanceOf(FireCommand.class);
        command2.get().execute();
        verify(object2, times(1)).setProperty(eq("fireDirection"), eq(fireDirection));
    }

    @Test
    void throwExceptionIfNotCorrectOrderTest() {
        UObject order = mock(UObject.class);

        when(order.getProperty(eq("action"))).thenReturn("abc");
        assertThatThrownBy(() -> {
            new CommandInterpreter(gameId1).interpret(order);
        }).isInstanceOf(InterpretException.class).hasMessage("Action not found");

        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), "abc", EndMoveCommand.class);
        assertThatThrownBy(() -> {
            new CommandInterpreter(gameId1).interpret(order);
        }).isInstanceOf(InterpretException.class).hasMessage("Command not found");

    }

    private void initUserScope(String username, Map<String, Map<String, UObject>> objectsInGames) {
        String scopeName = String.format("User.%s", username);
        ((Command) IoC.resolve("Scopes.New", IoC.resolve("Scopes.Current"), scopeName)).execute();
        ((Command) IoC.resolve("Scopes.Current.Set", scopeName)).execute();
        Map<String, UObject> objects = new ConcurrentHashMap<>();
        objectsInGames.forEach((gameId, gameObjects) -> {
            ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Add", gameId), (Function<Object[], Object>) args1 -> objects.put((String) args1[0], (UObject) args1[1]))).execute();
            ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Get", gameId), (Function<Object[], Object>) args1 -> objects.get((String) args1[0]))).execute();
            gameObjects.forEach((objectId, object) -> {
                IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object);
                IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object);
            });
        });
    }

    private static class OtherCommand implements Command {
        Integer param1;
        String param2;

        public OtherCommand(Integer param1, String param2) {
            this.param1 = param1;
            this.param2 = param2;
        }

        @Override
        public void execute() {

        }
    }
}
