package ru.otus.AdapterTests;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import ru.otus.adapter.AdapterGenerator;
import ru.otus.adapter.AdapterGeneratorImplementation;
import ru.otus.command.Command;
import ru.otus.domain.CurrentLocation;
import ru.otus.domain.IMovingObject;
import ru.otus.domain.UObject;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;
import ru.otus.test.AdapterProvider;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class GameIoCTests {
    private UObject uObject;

    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();
        uObject = mock(UObject.class);
    }

    @Test
    public void generateCorrectAdapterThatCallsCorrectMethodsTest() {
        IMovingObject iMovingObject = IoC.resolve("Adapter", IMovingObject.class, uObject);
        ((Command) IoC.resolve("IoC.Register", "IMovingObject:location.get", (Function<Object[], Object>) args -> ((UObject) args[0]).getProperty("location"))).execute();
        ((Command) IoC.resolve("IoC.Register", "IMovingObject:velocity.get", (Function<Object[], Object>) args -> ((UObject) args[0]).getProperty("velocity"))).execute();
        ((Command) IoC.resolve("IoC.Register", "IMovingObject:location.set", (Function<Object[], Object>) args -> {
            ((UObject) args[0]).setProperty("location", args[1]);
            return null;
        })).execute();

        CurrentLocation currentLocation = new CurrentLocation(1, 2, 3);

        iMovingObject.getLocation();
        verify(uObject, times(1)).getProperty("location");
        verify(uObject, times(0)).getProperty("velocity");
        verify(uObject, times(0)).setProperty("location", currentLocation);

        iMovingObject.getVelocity();
        verify(uObject, times(1)).getProperty("location");
        verify(uObject, times(1)).getProperty("velocity");
        verify(uObject, times(0)).setProperty("location", currentLocation);

        iMovingObject.setLocation(currentLocation);
        verify(uObject, times(1)).getProperty("location");
        verify(uObject, times(1)).getProperty("velocity");
        verify(uObject, times(1)).setProperty("location", currentLocation);
    }


    @Test
    public void addAdapterGeneratorOnceInOneThread() {
        AdapterProvider adapterProvider = spy(new AdapterProvider());
        ((Command) IoC.resolve("IoC.Register", "Adapter.Generator.Create", (Function<Object[], Object>) args -> adapterProvider.generate())).execute();

        assertThatThrownBy(() -> {
            IoC.resolve("Adapter.Generator");
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key Adapter.Generator");

        IMovingObject iMovingObject = IoC.resolve("Adapter", IMovingObject.class, uObject);
        AdapterGenerator adapterGenerator1 = IoC.resolve("Adapter.Generator");
        assertThat(adapterGenerator1).isInstanceOf(AdapterGeneratorImplementation.class);

        IMovingObject iMovingObject2 = IoC.resolve("Adapter", IMovingObject.class, uObject);
        AdapterGenerator adapterGenerator2 = IoC.resolve("Adapter.Generator");
        assertThat(adapterGenerator2).isInstanceOf(AdapterGeneratorImplementation.class).isSameAs(adapterGenerator1);
        verify(adapterProvider, times(1)).generate();

    }



}
