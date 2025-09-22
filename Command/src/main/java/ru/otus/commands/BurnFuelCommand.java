package ru.otus.commands;

import ru.otus.domain.IFuelBurnObject;
import ru.otus.exceptions.CommandException;

public class BurnFuelCommand implements Command {
    private final IFuelBurnObject iFuelBurnObject;

    public BurnFuelCommand(IFuelBurnObject iFuelBurnObject) {
        this.iFuelBurnObject = iFuelBurnObject;
    }

    @Override
    public void execute() {
        if (iFuelBurnObject == null) {
            throw new CommandException("Object is null");
        }
        int newLevel = iFuelBurnObject.getFuelLevel() - iFuelBurnObject.getFuelBurnVelocity();
        if (newLevel < 0) {
            throw new CommandException("You have not enough fuel");
        }
        iFuelBurnObject.setFuelLevel(newLevel);
    }
}
