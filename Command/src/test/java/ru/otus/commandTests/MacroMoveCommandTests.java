package ru.otus.commandTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.commands.Command;
import ru.otus.commands.MacroMoveCommand;
import ru.otus.commands.MoveCommand;
import ru.otus.commands.QueueCommand;
import ru.otus.exceptions.CommandException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MacroMoveCommandTests {

    private MoveCommand moveCommand;
    private QueueCommand queueCommand;
    private MacroMoveCommand macroMoveCommand;

    @BeforeEach
    void setUp() {
        moveCommand = mock(MoveCommand.class);
        queueCommand = mock(QueueCommand.class);
        macroMoveCommand = new MacroMoveCommand(moveCommand, queueCommand);
    }

    @Test
    public void TestExecuteCheckFuelCommandAndMoveBurnFuelCommand() {
        macroMoveCommand.execute();

        verify(moveCommand, times(1)).execute();
        verify(queueCommand, times(1)).addLast(argThat(c -> c == macroMoveCommand));

        InOrder inOrder = inOrder(moveCommand, queueCommand);
        inOrder.verify(moveCommand).execute();
        inOrder.verify(queueCommand).addLast(any(Command.class));
    }

    @Test
    public void TestStopMacroMoveCommand() {
        RuntimeException exception = new RuntimeException("test");
        doThrow(exception).when(moveCommand).execute();
        assertThatThrownBy(() -> {
            macroMoveCommand.execute();
        }).isInstanceOf(CommandException.class).hasMessageContaining(exception.getMessage()).hasCause(exception);

        verify(moveCommand, times(1)).execute();
        verify(queueCommand, times(0)).addLast(any(Command.class));
    }
}
