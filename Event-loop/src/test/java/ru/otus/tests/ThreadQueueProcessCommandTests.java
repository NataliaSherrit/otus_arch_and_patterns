package ru.otus.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.command.Command;
import ru.otus.command.ThreadQueueProcessCommand;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.queue.QueueHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ThreadQueueProcessCommandTests {
    private QueueHandler queueHandler;

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();

        queueHandler = mock(QueueHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Queue.Handler", (Function<Object[], Object>) args -> queueHandler)).execute();
    }

    @Test
    public void checkThatQueueStartProcessCommandInOtherThread() throws InterruptedException {
        CountDownLatch queueHandlerInvoked = new CountDownLatch(1);
        AtomicReference<String> queueHandlerThreadName = new AtomicReference<>();

        doAnswer(invocation -> {
            queueHandlerThreadName.set(Thread.currentThread().getName());
            queueHandlerInvoked.countDown();
            return null;
        }).when(queueHandler).handle();

        ThreadQueueProcessCommand command = new ThreadQueueProcessCommand();
        command.execute();

        boolean invoked = queueHandlerInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        assertThat(queueHandlerThreadName.get())
                .isNotNull()
                .isNotEqualTo(Thread.currentThread().getName());
    }

}
