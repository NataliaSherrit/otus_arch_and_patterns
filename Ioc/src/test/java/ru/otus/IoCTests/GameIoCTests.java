package ru.otus.IoCTests;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import ru.otus.command.Command;
import ru.otus.ioc.GameIoC;
import ru.otus.ioc.Strategy;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.test.Beast;
import ru.otus.test.Human;
import ru.otus.test.IMovingObject;
import ru.otus.test.MoveCommand;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.ioc.StrategyBasedOnScopes.DEFAULT_SCOPE_NAME;

public class GameIoCTests {
    private GameIoC gameIoC;
    private StrategyBasedOnScopes strategyBasedOnScopes;

    @BeforeEach
    void setUp() {
        gameIoC = new GameIoC();
        strategyBasedOnScopes = new StrategyBasedOnScopes(gameIoC);
    }

    @Test
    public void setCustomStrategyTest() {
        Object object = new Object();
        Strategy myStrategy = (key, args) -> object;
        ((Command) gameIoC.resolve("IoC.SetupStrategy", myStrategy)).execute();
        Object test = gameIoC.resolve("TEST");
        assertThat(test).isEqualTo(object);
    }
    @Test
    public void checkThatStrategyInitializedTest() {
        assertThatThrownBy(() -> gameIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Human((String) args[0]))).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key IoC.Register");
    }
    @Test
    public void registerAndResolveDependencyTest() {
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        String iMovingObjName = "Moving human";
        Command registerIMovingObject = gameIoC.resolve("IoC.Register", "IMovingObject", (Function<Object[], Object>) args -> new Human((String) args[0]));
        registerIMovingObject.execute();
        Command registerMove = gameIoC.resolve("IoC.Register", "Move", (Function<Object[], Object>) args -> new MoveCommand(gameIoC.resolve("IMovingObject", iMovingObjName)));
        registerMove.execute();

        Command moveCommand = gameIoC.resolve("Move");
        assertThat(moveCommand).isInstanceOf(MoveCommand.class);
        IMovingObject iMovingObject = ((MoveCommand) moveCommand).iMovingObject;
        assertThat(iMovingObject).isInstanceOf(Human.class);
        assertThat(iMovingObject.getName()).isEqualTo(iMovingObjName);
    }

    @Test
    public void operateDifferentScopesTest() {
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        String currentScope = gameIoC.resolve("Scopes.Current");
        assertThat(currentScope).isEqualTo(DEFAULT_SCOPE_NAME);

        assertThatThrownBy(() -> ((Command) gameIoC.resolve("Scopes.New", "scopeId", "test")).execute()).isInstanceOf(IllegalArgumentException.class).hasMessage("Parent scope scopeId not found");

        String newScopeName = "ABC";
        ((Command) gameIoC.resolve("Scopes.New", DEFAULT_SCOPE_NAME, newScopeName)).execute();

        assertThatThrownBy(() -> ((Command) gameIoC.resolve("Scopes.New", DEFAULT_SCOPE_NAME, newScopeName)).execute()).isInstanceOf(IllegalArgumentException.class).hasMessage("Scope ABC already exists");

        ((Command) gameIoC.resolve("Scopes.Current.Set", newScopeName)).execute();
        String currentScope2 = gameIoC.resolve("Scopes.Current");
        assertThat(currentScope2).isEqualTo(newScopeName);

        ((Command) gameIoC.resolve("Scopes.Current.Set", DEFAULT_SCOPE_NAME)).execute();
        String currentScope3 = gameIoC.resolve("Scopes.Current");
        assertThat(currentScope3).isEqualTo(DEFAULT_SCOPE_NAME);
    }

    @Test
    public void operateInDifferentThreadsTest() throws InterruptedException {
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();
        String[] movableNames = {"Moving human", "Moving beast"};
        String[] scopeNames = {"Scope1", "Scope2"};

        Function<Integer, Runnable> runnable = i -> () -> {
            ((Command) gameIoC.resolve("Scopes.New", DEFAULT_SCOPE_NAME, scopeNames[i])).execute();
            ((Command) gameIoC.resolve("Scopes.Current.Set", scopeNames[i])).execute();
            String currentScope = gameIoC.resolve("Scopes.Current");
            assertThat(currentScope).isEqualTo(scopeNames[i]);

            ((Command) gameIoC.resolve("IoC.Register", "IMovingObject", (Function<Object[], Object>) args -> {
                if (i == 0) {
                    return new Human((String) args[0]);
                } else {
                    return new Beast((String) args[0]);
                }
            })).execute();

            IMovingObject iMovingObject = gameIoC.resolve("IMovingObject", movableNames[i]);
            if (i == 0) {
                assertThat(iMovingObject).isInstanceOf(Human.class);
            } else {
                assertThat(iMovingObject).isInstanceOf(Beast.class);
            }
            assertThat(iMovingObject.getName()).isEqualTo(movableNames[i]);
        };
        Thread thread = new Thread(runnable.apply(0));
        thread.start();

        runnable.apply(1).run();
        thread.join();
        IMovingObject iMovingObject = gameIoC.resolve("IMovingObject", movableNames[1]);
        assertThat(iMovingObject).isInstanceOf(Beast.class);
        assertThat(iMovingObject.getName()).isEqualTo(movableNames[1]);
    }

}
