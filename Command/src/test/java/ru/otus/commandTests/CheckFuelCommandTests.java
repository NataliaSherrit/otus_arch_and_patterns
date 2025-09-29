package ru.otus.commandTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.commands.CheckFuelCommand;
import ru.otus.domain.IFuelCheckObject;
import ru.otus.exceptions.CommandException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CheckFuelCommandTests {
    private IFuelCheckObject iFuelCheckObject;
    private CheckFuelCommand checkFuelCommand;

    @BeforeEach
    void setUp() {
        iFuelCheckObject = mock(IFuelCheckObject.class);
        checkFuelCommand = new CheckFuelCommand(iFuelCheckObject);
    }

    @Test
    public void testCheckingEnoughFuelToBurn() {
        when(iFuelCheckObject.getFuelLevel()).thenReturn(10);
        when(iFuelCheckObject.getFuelBurnVelocity()).thenReturn(2);
        checkFuelCommand.execute();

        verify(iFuelCheckObject, times(1)).getFuelLevel();
        verify(iFuelCheckObject, times(1)).getFuelBurnVelocity();
    }

    @Test
    public void testCheckingNotEnoughFuelToBurn() {
        when(iFuelCheckObject.getFuelLevel()).thenReturn(1);
        when(iFuelCheckObject.getFuelBurnVelocity()).thenReturn(2);
        assertThatThrownBy(() -> checkFuelCommand.execute()).isInstanceOf(CommandException.class).hasMessageContaining("Not enough fuel");
    }
}
