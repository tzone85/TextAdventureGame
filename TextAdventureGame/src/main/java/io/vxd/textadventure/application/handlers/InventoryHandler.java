package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class InventoryHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        Set<String> inventoryItems = worldState.itemLocations().getOrDefault("inventory", Set.of());

        if (inventoryItems.isEmpty()) {
            return new CommandResult("You are not carrying anything.");
        }

        StringBuilder output = new StringBuilder("You are carrying:\n");
        for (String itemId : inventoryItems) {
            Item item = worldDefinition.items().get(itemId);
            if (item != null && !item.names().isEmpty()) {
                output.append("- ").append(item.names().get(0)).append("\n");
            }
        }

        return new CommandResult(output.toString().trim());
    }
}
