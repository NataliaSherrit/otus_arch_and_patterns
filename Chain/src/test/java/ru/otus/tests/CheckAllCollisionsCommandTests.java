package ru.otus.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.command.CheckAllCollisionsCommand;
import ru.otus.command.Command;
import ru.otus.domain.GameArea;
import ru.otus.domain.GameMap;
import ru.otus.domain.GameMapImpl;
import ru.otus.ioc.IoC;
import ru.otus.ioc.StrategyBasedOnScopes;

import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.*;

public class CheckAllCollisionsCommandTests {
    @BeforeEach
    void setUp() {
        StrategyBasedOnScopes strategyBasedOnScopes = new StrategyBasedOnScopes();
        (strategyBasedOnScopes.new InitScopeBasedIoCCommand()).execute();
    }
    @Test
    void handleCollisionsForAllGameMapsAndGameAreasTest() {
        GameArea area1 = mock(GameArea.class);
        GameArea area2 = mock(GameArea.class);
        GameArea area3 = mock(GameArea.class);
        GameArea area4 = mock(GameArea.class);
        GameArea area5 = mock(GameArea.class);
        GameArea area6 = mock(GameArea.class);
        GameMap gameMap3 = spy(new GameMapImpl(List.of(area1, area2), null));
        GameMap gameMap2 = spy(new GameMapImpl(List.of(area3), gameMap3));
        GameMap gameMap1 = spy(new GameMapImpl(List.of(area4, area5, area6), gameMap2));
        ((Command) IoC.resolve("IoC.Register","GameMap", (Function<Object[], Object>) args -> gameMap1)).execute();

        CheckAllCollisionsCommand command = new CheckAllCollisionsCommand();
        command.execute();
        verify(gameMap1, times(1)).handleCollisions();
        verify(gameMap2, times(1)).handleCollisions();
        verify(gameMap3, times(1)).handleCollisions();
        verify(area1, times(1)).handleCollisions();
        verify(area2, times(1)).handleCollisions();
        verify(area3, times(1)).handleCollisions();
        verify(area4, times(1)).handleCollisions();
        verify(area5, times(1)).handleCollisions();
        verify(area6, times(1)).handleCollisions();
    }
}
