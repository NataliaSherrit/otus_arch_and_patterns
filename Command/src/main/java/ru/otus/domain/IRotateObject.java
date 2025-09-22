package ru.otus.domain;

import org.jetbrains.annotations.NotNull;
@NotNull
public interface IRotateObject {

    int getDirection();

    void setDirection(int direction);

    int getAngular();

    int getNumOfDirections();

}
