package ru.otus;

public class CurrentLocation {
    private final int[] currentLocation;

    public CurrentLocation (int... currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getCurrentLocation(int size){
        if (size < 0 || size >= currentLocation.length) {
            throw new IllegalArgumentException();
        }
        return currentLocation[size];
    }

    public int getSize() {
        return currentLocation.length;
    }

    private static void checkPlusIsPossible(CurrentLocation currentLocation, CurrentLocation shiftLocation){
        if (currentLocation.getSize() != shiftLocation.getSize()) {
            throw new IllegalStateException();
        }
    }

    public static CurrentLocation plus(CurrentLocation currentLocation, CurrentLocation shiftLocation){
        checkPlusIsPossible(currentLocation, shiftLocation);
        int[] vector = new int[currentLocation.getSize()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = currentLocation.getCurrentLocation(i) + shiftLocation.getCurrentLocation(i);
        }
        return new CurrentLocation(vector);
    }

}
