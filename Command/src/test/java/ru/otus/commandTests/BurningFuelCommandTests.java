package ru.otus.commandTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.commands.BurnFuelCommand;
import ru.otus.domain.IFuelBurnObject;
import ru.otus.exceptions.CommandException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BurningFuelCommandTests {
    private IFuelBurnObject iFuelBurnObject;
    private BurnFuelCommand burnFuelCommand;

    @BeforeEach
    void setUp() {
        iFuelBurnObject = mock(IFuelBurnObject.class);
        burnFuelCommand = new BurnFuelCommand(iFuelBurnObject);
    }

    @Test
    public void testWhenEnoughFuelToBurn() {
        when(iFuelBurnObject.getFuelLevel()).thenReturn(10);
        when(iFuelBurnObject.getFuelBurnVelocity()).thenReturn(2);
        burnFuelCommand.execute();

        verify(iFuelBurnObject, times(1)).setFuelLevel(eq(8));
    }

    @Test
    public void testWhenNotEnoughFuelToBurn() {
        when(iFuelBurnObject.getFuelLevel()).thenReturn(1);
        when(iFuelBurnObject.getFuelBurnVelocity()).thenReturn(2);
        assertThatThrownBy(() -> burnFuelCommand.execute()).isInstanceOf(CommandException.class).hasMessageContaining("You have not enough fuel");
    }
}
