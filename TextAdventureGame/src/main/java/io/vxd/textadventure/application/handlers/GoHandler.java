package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class GoHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        if (command.target() == null || command.target().isEmpty()) {
            return new CommandResult("Go where?");
        }

        String currentRoomId = worldState.currentRoomId();
        Room currentRoom = worldDefinition.rooms().get(currentRoomId);

        if (currentRoom == null) {
            return new CommandResult("You are nowhere.");
        }

        String direction = command.target().toLowerCase();
        String nextRoomId = currentRoom.exits().get(direction);

        if (nextRoomId == null) {
            return new CommandResult("You cannot go that way.");
        }

        Room nextRoom = worldDefinition.rooms().get(nextRoomId);
        if (nextRoom == null) {
            return new CommandResult("The room does not exist.");
        }

        // Move to the new room
        worldState.setCurrentRoomId(nextRoomId);

        return new CommandResult("You go " + direction + " to " + nextRoom.name() + ".");
    }
}
