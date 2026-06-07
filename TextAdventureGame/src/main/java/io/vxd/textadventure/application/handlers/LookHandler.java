package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class LookHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        if (command.target() != null) {
            // LOOK IN container
            return lookInContainer(command.target(), worldState, worldDefinition);
        }

        // LOOK at current room
        return lookAtRoom(worldState, worldDefinition);
    }

    private CommandResult lookAtRoom(WorldState worldState, WorldDefinition worldDefinition) {
        String currentRoomId = worldState.currentRoomId();
        Room room = worldDefinition.rooms().get(currentRoomId);
        if (room == null) {
            return new CommandResult("You are in an unknown location.");
        }

        StringBuilder output = new StringBuilder();
        output.append(room.name()).append("\n");
        output.append(room.description()).append("\n");

        // List visible items
        Set<String> itemsInRoom = worldState.itemLocations().getOrDefault(currentRoomId, Set.of());
        if (!itemsInRoom.isEmpty()) {
            output.append("\nYou see: ");
            List<String> itemNames = new ArrayList<>();
            for (String itemId : itemsInRoom) {
                Item item = worldDefinition.items().get(itemId);
                if (item != null && !item.names().isEmpty()) {
                    itemNames.add(item.names().get(0));
                }
            }
            output.append(String.join(", ", itemNames)).append("\n");
        }

        // List NPCs
        List<String> npcIds = room.npcIds();
        if (!npcIds.isEmpty()) {
            output.append("NPCs: ");
            List<String> npcNames = new ArrayList<>();
            for (String npcId : npcIds) {
                Npc npc = worldDefinition.npcs().get(npcId);
                if (npc != null) {
                    npcNames.add(npc.name());
                }
            }
            output.append(String.join(", ", npcNames)).append("\n");
        }

        // List exits
        if (!room.exits().isEmpty()) {
            output.append("Exits: ").append(String.join(", ", room.exits().keySet())).append("\n");
        }

        return new CommandResult(output.toString().trim());
    }

    private CommandResult lookInContainer(String containerName, WorldState worldState, WorldDefinition worldDefinition) {
        // Find the container item in inventory or current room
        Item container = null;
        String containerId = null;

        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && item.matchesName(containerName)) {
                container = item;
                containerId = itemId;
                break;
            }
        }

        if (container == null) {
            String currentRoomId = worldState.currentRoomId();
            Set<String> roomItems = worldState.itemLocations().getOrDefault(currentRoomId, Set.of());
            for (String itemId : roomItems) {
                Item item = worldDefinition.items().get(itemId);
                if (item != null && item.matchesName(containerName)) {
                    container = item;
                    containerId = itemId;
                    break;
                }
            }
        }

        if (container == null || !container.container()) {
            return new CommandResult("You cannot look inside that.");
        }

        // List contents of container
        Set<String> contents = worldState.itemLocations().getOrDefault(containerId, Set.of());
        if (contents.isEmpty()) {
            return new CommandResult(container.names().get(0) + " is empty.");
        }

        StringBuilder output = new StringBuilder();
        output.append("Inside the ").append(container.names().get(0)).append(":\n");
        List<String> contentNames = new ArrayList<>();
        for (String itemId : contents) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && !item.names().isEmpty()) {
                contentNames.add(item.names().get(0));
            }
        }
        output.append(String.join(", ", contentNames));

        return new CommandResult(output.toString());
    }
}
