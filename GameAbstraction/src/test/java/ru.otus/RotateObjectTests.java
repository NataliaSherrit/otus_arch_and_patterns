package ru.otus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class RotateObjectTests {
    private Rotate rotate;
    private IRotateObject rotateObject;

    @BeforeEach
    void setUp() {
        rotateObject = mock(IRotateObject.class);
        rotate = new Rotate(rotateObject);
    }

    @Test
    public void testCorrectRotation() {
        when(rotateObject.getNumOfDirections()).thenReturn(360);
        when(rotateObject.getDirection()).thenReturn(350);
        when(rotateObject.getAngular()).thenReturn(60);
        rotate.execute();

        verify(rotateObject, times(1)).setDirection(eq(50));
    }

    @Test
    public void testNotNullNumOfDirections() {
        when(rotateObject.getNumOfDirections()).thenReturn(0);
        when(rotateObject.getDirection()).thenReturn(200);
        when(rotateObject.getAngular()).thenReturn(50);
        assertThatThrownBy(() -> {
            rotate.execute();
        }).isInstanceOf(ArithmeticException.class).hasMessageContaining("by zero");
    }

    @Test
    public void testCanNotGetDirection() {
        when(rotateObject.getNumOfDirections()).thenReturn(50);
        when(rotateObject.getDirection()).thenThrow(new RuntimeException("Can not get direction"));
        when(rotateObject.getAngular()).thenReturn(10);
        assertThatThrownBy(() -> {
            rotate.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not get direction");
    }

    @Test
    public void testCanNotGetAngular() {
        when(rotateObject.getNumOfDirections()).thenReturn(360);
        when(rotateObject.getDirection()).thenReturn(100);
        when(rotateObject.getAngular()).thenThrow(new RuntimeException("Can not get angular"));
        assertThatThrownBy(() -> {
            rotate.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not get angular");
    }


    @Test
    public void testCanNotSetDirection() {
        when(rotateObject.getNumOfDirections()).thenReturn(360);
        when(rotateObject.getDirection()).thenReturn(100);
        when(rotateObject.getAngular()).thenReturn(20);
        doThrow(new RuntimeException("Can not set direction")).when(rotateObject).setDirection(anyInt());
        assertThatThrownBy(() -> {
            rotate.execute();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("Can not set direction");
    }
}
