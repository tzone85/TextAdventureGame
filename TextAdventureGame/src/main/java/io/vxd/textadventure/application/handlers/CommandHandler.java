package io.vxd.textadventure.application.handlers;

import io.vxd.textadventure.application.Command;
import io.vxd.textadventure.application.CommandResult;
import io.vxd.textadventure.domain.WorldDefinition;
import io.vxd.textadventure.domain.WorldState;

public interface CommandHandler {
    CommandResult handle(Command command, WorldState worldState, WorldDefinition worldDefinition);
}
