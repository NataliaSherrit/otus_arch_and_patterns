package ru.otus.commands;
import ru.otus.exceptions.CommandException;
import ru.otus.domain.IFuelCheckObject;

public class CheckFuelCommand implements Command {
    private final IFuelCheckObject iFuelCheckObject;

    public CheckFuelCommand(IFuelCheckObject iFuelCheckObject) {
        this.iFuelCheckObject = iFuelCheckObject;
    }

    @Override
    public void execute() {
        if (iFuelCheckObject == null) {
            throw new CommandException("Object is null");
        }
        if (iFuelCheckObject.getFuelLevel() < iFuelCheckObject.getFuelBurnVelocity()) {
            throw new CommandException("Not enough fuel");
        }
    }
}
