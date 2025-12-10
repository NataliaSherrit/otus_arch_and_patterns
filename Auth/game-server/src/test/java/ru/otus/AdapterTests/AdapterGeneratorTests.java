package ru.otus.AdapterTests;

import org.junit.jupiter.api.BeforeEach;
import ru.otus.adapter.AdapterGeneratorImplementation;
import ru.otus.domain.IMovingObject;
import ru.otus.domain.UObject;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

public class AdapterGeneratorTests {
    private AdapterGeneratorImplementation adapterGeneratorImplementation;
    private UObject uObject;

    @BeforeEach
    void setUp() {
        adapterGeneratorImplementation = new AdapterGeneratorImplementation();
        uObject = mock(UObject.class);
    }

    @Test
    public void shouldGenerateValidAdapter() {
        IMovingObject iMovingObject = adapterGeneratorImplementation.resolve(IMovingObject.class, uObject);
        assertThat(iMovingObject.getClass().getName()).isEqualTo("ru.otus.adapter.IMovingObjectAdapter");
        assertThat(iMovingObject.getClass().getDeclaredMethods().length).isEqualTo(3);
    }
}
