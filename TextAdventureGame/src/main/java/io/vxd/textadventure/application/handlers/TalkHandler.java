package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class TalkHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        if (command.target() == null || command.target().isEmpty()) {
            return new CommandResult("Talk to whom?");
        }

        String currentRoomId = worldState.currentRoomId();
        Room currentRoom = worldDefinition.rooms().get(currentRoomId);

        if (currentRoom == null) {
            return new CommandResult("You are nowhere.");
        }

        // Find NPC in the room
        Npc npc = null;
        for (String npcId : currentRoom.npcIds()) {
            Npc candidate = worldDefinition.npcs().get(npcId);
            if (candidate != null && candidate.name().equalsIgnoreCase(command.target())) {
                npc = candidate;
                break;
            }
        }

        if (npc == null) {
            return new CommandResult("You don't see that person here.");
        }

        // Return the first dialogue node
        return new CommandResult(npc.conversationTree().text());
    }
}
