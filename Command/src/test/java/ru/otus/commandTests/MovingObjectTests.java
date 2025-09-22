package ru.otus.commandTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.commands.MoveCommand;
import ru.otus.domain.IMovingObject;
import ru.otus.domain.CurrentLocation;
import ru.otus.exceptions.CommandException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MovingObjectTests {
    private MoveCommand moveCommand;
    private IMovingObject movingObject;

    @BeforeEach
    void setUp() {
        movingObject = mock(IMovingObject.class);
        moveCommand = new MoveCommand(movingObject);
    }

    @Test
    public void testCorrectMove() {
        when(movingObject.getLocation()).thenReturn(new CurrentLocation(12, 5));
        when(movingObject.getVelocity()).thenReturn(new CurrentLocation(-7, 3));
        moveCommand.execute();

        verify(movingObject, times(1)).setLocation(argThat(currentLocation -> currentLocation.getCurrentLocation(0) == 5 && currentLocation.getCurrentLocation(1) == 8));
    }

    @Test
    public void testNotNullLocation() {
        when(movingObject.getLocation()).thenReturn(null);
        assertThatThrownBy(() -> {moveCommand.execute();}).isInstanceOf(CommandException.class).hasMessageContaining("Can not get location");
    }

    @Test
    public void testNotNullVelocity() {
        when(movingObject.getLocation()).thenReturn(new CurrentLocation(12, 5));
        when(movingObject.getVelocity()).thenReturn(null);
        assertThatThrownBy(() -> {
            moveCommand.execute();
        }).isInstanceOf(CommandException.class).hasMessageContaining("Can not get velocity");
    }

    @Test
    public void testCanNotSetPosition() {
        when(movingObject.getLocation()).thenReturn(new CurrentLocation(12, 5));
        when(movingObject.getVelocity()).thenReturn(new CurrentLocation(-7, 3));
        doThrow(new CommandException("Can not set position")).when(movingObject).setLocation(any());
        assertThatThrownBy(() -> {
            moveCommand.execute();
        }).isInstanceOf(CommandException.class).hasMessageContaining("Can not set position");
    }

}