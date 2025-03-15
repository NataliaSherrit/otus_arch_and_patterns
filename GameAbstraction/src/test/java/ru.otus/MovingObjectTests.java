package ru.otus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MovingObjectTests {
    private Move move;
    private IMovingObject movingObject;

    @BeforeEach
    void setUp() {
        movingObject = mock(IMovingObject.class);
        move = new Move(movingObject);
    }

    @Test
    public void testCorrectMove() {
        when(movingObject.getLocation()).thenReturn(new CurrentLocation(12, 5));
        when(movingObject.getVelocity()).thenReturn(new CurrentLocation(-7, 3));
        move.execute();

        verify(movingObject, times(1)).setLocation(argThat(currentLocation -> currentLocation.getX() == 5 && currentLocation.getY() == 8));
    }

    @Test
    public void testNotNullLocation() {
        when(movingObject.getLocation()).thenReturn(null);
        assertThatThrownBy(() -> {move.execute();}).isInstanceOf(NullPointerException.class).hasMessageContaining("\"location\" is null");
    }

    @Test
    public void testNotNullVelocity() {
        when(movingObject.getLocation()).thenReturn(new CurrentLocation(12, 5));
        when(movingObject.getVelocity()).thenReturn(null);
        assertThatThrownBy(() -> {
            move.execute();
        }).isInstanceOf(NullPointerException.class).hasMessageContaining("\"velocity\" is null");
    }

    @Test
    public void testCanNotSetPosition() {
        when(movingObject.getLocation()).thenReturn(new CurrentLocation(12, 5));
        when(movingObject.getVelocity()).thenReturn(new CurrentLocation(-7, 3));
        doThrow(new RuntimeException("Can not set position")).when(movingObject).setLocation(any());
        assertThatThrownBy(() -> {
            move.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not set position");
    }

}