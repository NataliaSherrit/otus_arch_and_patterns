package ru.otus.commands;

import ru.otus.CurrentLocation;
import ru.otus.IMovingObject;

public class MoveCommand implements Command{
    private final IMovingObject object;
    public MoveCommand(IMovingObject obj){
        object = obj;
    }

    public void execute(){
        object.setLocation(CurrentLocation.plus(object.getLocation(), object.getVelocity()));
    }

}
