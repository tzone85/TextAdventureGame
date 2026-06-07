package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.*;

import java.util.*;

public class UseHandler implements CommandHandler {
    @Override
    public CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        if (command.target() == null || command.target().isEmpty()) {
            return new CommandResult("Use what?");
        }

        if (command.container() == null || command.container().isEmpty()) {
            return new CommandResult("Use it on what?");
        }

        // For now, return a simple message - this would integrate with event engine
        return new CommandResult("You try to use the " + command.target() + " on the " + command.container() + ".");
    }
}
