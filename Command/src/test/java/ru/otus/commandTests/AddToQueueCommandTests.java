package ru.otus.commandTests;

import org.junit.jupiter.api.Test;
import ru.otus.commands.AddToQueueCommand;
import ru.otus.commands.Command;
import ru.otus.commands.QueueCommand;

import static org.mockito.Mockito.*;

public class AddToQueueCommandTests {
    @Test
    public void testAddToQueueOtherCommand() {
        Command command = mock(Command.class);
        QueueCommand queueCommand = mock(QueueCommand.class);
        AddToQueueCommand addToQueueCommand = new AddToQueueCommand(command, queueCommand);
        addToQueueCommand.execute();
        verify(queueCommand, times(1)).addLast(argThat(c -> c == command));
    }
}
