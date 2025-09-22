package ru.otus;

import org.jetbrains.annotations.NotNull;
@NotNull
public interface IMovingObject {
    @NotNull
    CurrentLocation getLocation();

    @NotNull
    CurrentLocation getVelocity();

    void setLocation(CurrentLocation newCurrentLocation);
}
