package ru.otus.command;

import ru.otus.domain.Area;
import ru.otus.domain.AreaObject;
import ru.otus.domain.GameMap;
import ru.otus.domain.Location;
import ru.otus.ioc.IoC;

import java.util.Map;

public class ChangeCommand implements Command {

    private final AreaObject object;
    private final Location previousLocation;

    public ChangeCommand(AreaObject object, Location previousLocation) {
        this.object = object;
        this.previousLocation = previousLocation;
    }

    @Override
    public void execute() {
        GameMap gameMap = IoC.resolve("GameMap");
        Map<String, Area> previousAreas;
        if (previousLocation != null) {
            previousAreas = gameMap.getAreas(previousLocation);
        } else {
            previousAreas = null;
        }
        Map<String, Area> currentAreas = gameMap.getAreas(object.getLocation());

        if (previousAreas != null) {
            previousAreas.forEach((key, value) -> {
                if (!currentAreas.containsKey(key)) {
                    value.removeObject(object);
                }
            });
        }
        currentAreas.forEach((key, value) -> {
            if (previousAreas == null || !previousAreas.containsKey(key)) {
                value.addObject(object);
            }
        });
    }
}