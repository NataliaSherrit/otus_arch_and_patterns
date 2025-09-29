package ru.otus.commands;
import java.util.List;

public class BurnFuelMoveCommand extends MacroCommand {

    public BurnFuelMoveCommand(CheckFuelCommand checkFuelCommand,
                               MoveCommand moveCommand,
                               BurnFuelCommand burnFuelCommand) {
        super(List.of(checkFuelCommand, moveCommand, burnFuelCommand));
    }
}
