package ru.otus.commands;


import ru.otus.domain.CurrentLocation;
import ru.otus.domain.IMovingObject;
import ru.otus.exceptions.CommandException;

public class MoveCommand implements Command {
    private final IMovingObject object;
    public MoveCommand(IMovingObject obj){
        object = obj;
    }

    public void execute(){

        if (object == null) {
            throw new CommandException("Object is null");
        }
        if (object.getLocation() == null) {
            throw new CommandException("Can not get location");
        }
        if (object.getVelocity() == null) {
            throw new CommandException("Can not get velocity");
        }
        object.setLocation(CurrentLocation.plus(object.getLocation(), object.getVelocity()));
    }

}
