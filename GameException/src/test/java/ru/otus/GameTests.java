package ru.otus;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.otus.commands.*;
import ru.otus.exceptions.DoubleRepeatThenWriteToLogExceptionHandlerStrategy;
import ru.otus.game.Game;
import ru.otus.exceptions.RepeatThenWriteToLogExceptionHandlerStrategy;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GameTests {

    @Test
    public void testRepeatCommandThenWriteToLogException() {
        QueueCommand queueCommand = spy(new QueueCommandImplementation());
        Game game = new Game(queueCommand, new RepeatThenWriteToLogExceptionHandlerStrategy());
        RuntimeException thrownException = new RuntimeException("test exception");
        Command command = () -> { throw thrownException; };
        queueCommand.addCommandToEndOfQueue(command);
        game.gameProcess();
        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(queueCommand, times(3)).addCommandToEndOfQueue(commandCaptor.capture());
        List<Command> addedCommands = commandCaptor.getAllValues();
        assertThat(addedCommands.get(0)).isEqualTo(command);
        assertThat(addedCommands.get(1).getClass()).isEqualTo(RepeatCommand.class);
        assertThat(addedCommands.get(2).getClass()).isEqualTo(WriteToLogCommand.class);
    }

    @Test
    public void testDoubleRepeatCommandThenWriteToLogException() {
        QueueCommand queueCommand = spy(new QueueCommandImplementation());
        Game game = new Game(queueCommand, new DoubleRepeatThenWriteToLogExceptionHandlerStrategy());

        RuntimeException thrownException = new RuntimeException("test exception");
        Command command = () -> { throw thrownException; };
        queueCommand.addCommandToEndOfQueue(command);
        game.gameProcess();
        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(queueCommand, times(4)).addCommandToEndOfQueue(commandCaptor.capture());
        List<Command> addedCommands = commandCaptor.getAllValues();
        assertThat(addedCommands.get(0)).isEqualTo(command);
        assertThat(addedCommands.get(1).getClass()).isEqualTo(RepeatCommand.class);
        assertThat(addedCommands.get(2).getClass()).isEqualTo(DoubleRepeatCommand.class);
        assertThat(addedCommands.get(3).getClass()).isEqualTo(WriteToLogCommand.class);
    }
}
