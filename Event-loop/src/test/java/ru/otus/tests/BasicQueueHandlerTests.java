package ru.otus.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.command.*;
import ru.otus.exception.ExceptionHandler;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.queue.BasicQueueHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

public class BasicQueueHandlerTests {
    private BasicQueueHandler basicQueueHandler;
    private ExceptionHandler exceptionHandler;
    private CommandQueue commandQueue;
    private Command emptyCommand;

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        basicQueueHandler = new BasicQueueHandler();
        exceptionHandler = mock(ExceptionHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Exception.Handler", (Function<Object[], Object>) args1 -> exceptionHandler)).execute();
        commandQueue = mock(CommandQueue.class);
        ((Command) IoC.resolve("IoC.Register", "CommandQueue", (Function<Object[], Object>) args1 -> commandQueue)).execute();
        new ContinueCommand().execute();
        emptyCommand = mock(Command.class);
        ((Command) IoC.resolve("IoC.Register", "CommandQueue.EmptyCommand", (Function<Object[], Object>) args1 -> emptyCommand)).execute();
    }

    @Test
    public void checkThatExecIsEndlessByDefault() throws InterruptedException {
        CountDownLatch emptyCommandInvoked = new CountDownLatch(5);

        doAnswer(invocation -> {
            emptyCommandInvoked.countDown();
            return null;
        }).when(emptyCommand).execute();

        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invoked = emptyCommandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);
    }

    @Test
    public void checkThatExecIsFinishIfQueueIsEmptyAndCommandStop() throws InterruptedException {
        Command command1 = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command1);

        CountDownLatch command1Invoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            command1Invoked.countDown();
            return null;
        }).when(command1).execute();

        CountDownLatch queueHandlerStopped = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle();
            queueHandlerStopped.countDown();
        });
        thread.start();

        boolean invokedCommand1 = command1Invoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand1).isEqualTo(true);

        new StopCommand().execute();

        Command command2 = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command2);

        CountDownLatch command2Invoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            command2Invoked.countDown();
            return null;
        }).when(command2).execute();

        boolean invokedCommand2 = command2Invoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand2).isEqualTo(true);

        when(commandQueue.readFirst()).thenReturn(null);

        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

    @Test
    public void checkThatExecIsFinishImmediatelyWhenHardStop() throws InterruptedException {
        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();
        CountDownLatch queueHandlerStopped = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle();
            queueHandlerStopped.countDown();
        });
        thread.start();
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        new HardStopCommand().execute();
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

}
