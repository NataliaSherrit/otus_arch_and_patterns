package ru.otus.commandTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.commands.BurnFuelCommand;
import ru.otus.commands.BurnFuelMoveCommand;
import ru.otus.commands.CheckFuelCommand;
import ru.otus.commands.MoveCommand;
import ru.otus.exceptions.CommandException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BurningFuelAndMovingCommandTests {
    private CheckFuelCommand checkFuelCommand;
    private MoveCommand moveCommand;
    private BurnFuelCommand burnFuelCommand;
    private BurnFuelMoveCommand burnFuelMoveCommand;

    @BeforeEach
    void setUp() {
        checkFuelCommand = mock(CheckFuelCommand.class);
        moveCommand = mock(MoveCommand.class);
        burnFuelCommand = mock(BurnFuelCommand.class);
        burnFuelMoveCommand = new BurnFuelMoveCommand(checkFuelCommand, moveCommand, burnFuelCommand);
    }

    @Test
    public void TestExecuteCheckFuelAndMoveBurnFuelCommands() {
        burnFuelMoveCommand.execute();

        verify(checkFuelCommand, times(1)).execute();
        verify(moveCommand, times(1)).execute();
        verify(burnFuelCommand, times(1)).execute();

        InOrder inOrder = inOrder(checkFuelCommand, moveCommand, burnFuelCommand);
        inOrder.verify(checkFuelCommand).execute();
        inOrder.verify(moveCommand).execute();
        inOrder.verify(burnFuelCommand).execute();
    }

    @Test
    public void TestStopMacroCommandIfOneIsUncorrect() {
        RuntimeException exception = new RuntimeException("test");
        doThrow(exception).when(checkFuelCommand).execute();
        assertThatThrownBy(() -> {
            burnFuelMoveCommand.execute();
        }).isInstanceOf(CommandException.class).hasMessageContaining(exception.getMessage()).hasCause(exception);

        verify(checkFuelCommand, times(1)).execute();
        verify(moveCommand, times(0)).execute();
        verify(burnFuelCommand, times(0)).execute();
    }
}
