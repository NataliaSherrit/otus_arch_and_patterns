package ru.otus.command;

import ru.otus.domain.GameMap;
import ru.otus.domain.Location;
import ru.otus.ioc.IoC;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class GameMapsInitCommand implements Command {

    private final int[] shifts;
    private final int areaSize;
    private final Location location;

    public GameMapsInitCommand(int[] shifts, int areaSize, Location location) {
        this.shifts = shifts;
        this.areaSize = areaSize;
        this.location = location;
    }

    @Override
    public void execute() {
        AtomicReference<GameMap> gameMapNext = new AtomicReference<>();
        Arrays.stream(shifts).boxed().sorted(Collections.reverseOrder()).forEach(shift -> {
            gameMapNext.set(IoC.resolve("GameMap.Obtain", shift, areaSize, location, gameMapNext.get()));
        });
        ((Command) IoC.resolve("IoC.Register", "GameMap", (Function<Object[], Object>) args -> gameMapNext.get())).execute();
    }
}