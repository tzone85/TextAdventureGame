package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class TakeHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        if (command.target() == null || command.target().isEmpty()) {
            return new CommandResult("Take what?");
        }

        String currentRoomId = worldState.currentRoomId();
        Set<String> itemsInRoom = worldState.itemLocations().getOrDefault(currentRoomId, Set.of());

        // Find matching items
        List<String> matchingIds = new ArrayList<>();
        for (String itemId : itemsInRoom) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && item.matchesName(command.target())) {
                matchingIds.add(itemId);
            }
        }

        if (matchingIds.isEmpty()) {
            return new CommandResult("You don't see that here.");
        }

        if (matchingIds.size() > 1) {
            StringBuilder msg = new StringBuilder("Multiple items match '").append(command.target()).append("':\n");
            for (String itemId : matchingIds) {
                Item item = worldDefinition.items().get(itemId);
                if (item != null) {
                    msg.append("- ").append(item.names().get(0)).append("\n");
                }
            }
            String output = msg.toString().trim();
            return new CommandResult(output, false, Optional.empty(), Optional.of(output));
        }

        String itemId = matchingIds.get(0);
        Item item = worldDefinition.items().get(itemId);

        // Check if takeable
        if (!item.takeable()) {
            return new CommandResult("You cannot take that.");
        }

        // Check weight limit
        if (worldDefinition.inventoryWeightLimit().isPresent()) {
            int weightLimit = worldDefinition.inventoryWeightLimit().get();
            int currentWeight = calculateInventoryWeight(worldState, worldDefinition);
            if (currentWeight + item.weight() > weightLimit) {
                return new CommandResult("That's too heavy to carry.");
            }
        }

        // Move item to inventory
        worldState.moveItem(itemId, currentRoomId, "inventory");

        return new CommandResult("You take the " + item.names().get(0) + ".");
    }

    private int calculateInventoryWeight(WorldState worldState, WorldDefinition worldDefinition) {
        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());
        int weight = 0;
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null) {
                weight += item.weight();
            }
        }
        return weight;
    }
}
