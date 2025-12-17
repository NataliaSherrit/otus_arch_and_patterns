package ru.otus.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.command.*;
import ru.otus.handler.BasicQueueHandler;
import ru.otus.handler.ExceptionHandler;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.state.DefaultState;
import ru.otus.state.MoveToState;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BasicQueueHandlerTest {

    private BasicQueueHandler basicQueueHandler;
    private ExceptionHandler exceptionHandler;
    private CommandQueue commandQueue;

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes scopeBasedStrategy = new StrategyBasedOnScopes();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
        commandQueue = mock(CommandQueue.class);
        exceptionHandler = mock(ExceptionHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Exception.Handler", (Function<Object[], Object>) args1 -> exceptionHandler)).execute();
    }

    @Test
    public void verifyThatProcessIsForeverByDefaultInDefaultStateTest() throws InterruptedException {
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(5);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();

        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);
    }

    @Test
    public void continueProcessQueueWhenExceptionIsThrownInDefaultStateTest() throws InterruptedException {
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(5);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            throw new IllegalArgumentException("TEST");
        }).when(command).execute();

        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        verify(exceptionHandler, atLeast(4)).handle(any(), any());
    }

    @Test
    public void processQueueEvenWhenEmptyInDefaultStateTest() throws InterruptedException {
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command).thenReturn(command).thenReturn(null).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();

        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invokedCommand = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand).isEqualTo(true);
    }

    @Test
    public void finishWhenHardStopInDefaultStateTest() throws InterruptedException {
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

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

        when(commandQueue.readFirst()).thenReturn(new HardStopCommand());
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

    @Test
    public void finishWhenHardStopInMoveToStateTest() throws InterruptedException {
        CommandQueue otherQueue = mock(CommandQueue.class);
        basicQueueHandler = new BasicQueueHandler(new MoveToState(otherQueue), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(otherQueue).addLast(eq(command));

        CountDownLatch queueHandlerStopped = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle();
            queueHandlerStopped.countDown();
        });
        thread.start();

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        when(commandQueue.readFirst()).thenReturn(new HardStopCommand()).thenReturn(command);
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

    @Test
    public void switchToMoveStateWhenReceivedMoveCommandTest() throws InterruptedException {
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        // очередь сначала переходит в режим MoveTo, а затем всегда возвращает command
        Command command = mock(Command.class);
        CommandQueue otherQueue = mock(CommandQueue.class);
        Command moveToCommand = spy(new MoveToCommand(otherQueue));
        when(commandQueue.readFirst()).thenReturn(moveToCommand).thenReturn(command);

        CountDownLatch moveToCommandInvoked = new CountDownLatch(1);
        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            moveToCommandInvoked.countDown();
            return invocation.callRealMethod();
        }).when(moveToCommand).execute();
        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(otherQueue).addLast(eq(command));

        Thread thread = new Thread(() -> basicQueueHandler.handle());
        thread.start();

        boolean moveToInvoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(moveToInvoked).isEqualTo(true);

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);
    }

    @Test
    public void switchToDefaultStateWhenReceivedRunCommandTest() throws InterruptedException {
        // создаём очередь с режимом MoveTo
        CommandQueue otherQueue = mock(CommandQueue.class);
        basicQueueHandler = new BasicQueueHandler(new MoveToState(otherQueue), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandMoveInvoked = new CountDownLatch(3);
        CountDownLatch commandExecInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandMoveInvoked.countDown();
            return null;
        }).when(otherQueue).addLast(eq(command));
        doAnswer(invocation -> {
            commandExecInvoked.countDown();
            return null;
        }).when(command).execute();

        Thread thread = new Thread(() -> basicQueueHandler.handle());
        thread.start();

        boolean invoked = commandMoveInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        when(commandQueue.readFirst()).thenReturn(new RunCommand()).thenReturn(command);
        boolean invokedQueueHandler = commandExecInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

}
