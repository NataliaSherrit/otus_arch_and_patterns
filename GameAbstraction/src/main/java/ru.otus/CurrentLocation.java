package ru.otus;

public class CurrentLocation {
    private final int x;
    private final int y;

    public CurrentLocation (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static CurrentLocation Plus(CurrentLocation location, CurrentLocation velocity){
        return new CurrentLocation(
                location.getX() + velocity.getX(),
                location.getY() + velocity.getY()
        );
    }
}
