package ru.otus;

public class Move {
    private final IMovingObject object;
    public Move(IMovingObject obj){
        object = obj;
    }

    public void execute(){
        object.setLocation(CurrentLocation.Plus(object.getLocation(), object.getVelocity()));
    }

}
