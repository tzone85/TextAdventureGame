package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class PutHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        if (command.target() == null || command.target().isEmpty()) {
            return new CommandResult("Put what?");
        }

        if (command.container() == null || command.container().isEmpty()) {
            return new CommandResult("Put it where?");
        }

        // Find the item to put
        Item item = findItemInInventory(command.target(), worldState, worldDefinition);
        String itemId = findItemIdInInventory(command.target(), worldState, worldDefinition);

        if (item == null) {
            return new CommandResult("You don't have that.");
        }

        // Find the container
        Item container = findContainer(command.container(), worldState, worldDefinition);
        String containerId = findContainerId(command.container(), worldState, worldDefinition);

        if (container == null) {
            return new CommandResult("You don't see that container.");
        }

        if (!container.container()) {
            return new CommandResult("You cannot put things in that.");
        }

        // Move item to container
        worldState.moveItem(itemId, "inventory", containerId);

        return new CommandResult("You put the " + item.names().get(0) + " in the " + container.names().get(0) + ".");
    }

    private Item findItemInInventory(String itemName, WorldState worldState, WorldDefinition worldDefinition) {
        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && item.matchesName(itemName)) {
                return item;
            }
        }
        return null;
    }

    private String findItemIdInInventory(String itemName, WorldState worldState, WorldDefinition worldDefinition) {
        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && item.matchesName(itemName)) {
                return itemId;
            }
        }
        return null;
    }

    private Item findContainer(String containerName, WorldState worldState, WorldDefinition worldDefinition) {
        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && item.matchesName(containerName)) {
                return item;
            }
        }
        return null;
    }

    private String findContainerId(String containerName, WorldState worldState, WorldDefinition worldDefinition) {
        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && item.matchesName(containerName)) {
                return itemId;
            }
        }
        return null;
    }
}
