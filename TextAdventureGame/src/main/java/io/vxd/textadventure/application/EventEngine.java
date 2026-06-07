package io.vxd.textadventure.application;

import io.vxd.textadventure.domain.*;

import java.util.*;

public class EventEngine {
    private final PredicateEvaluator predicateEvaluator;

    public EventEngine() {
        this.predicateEvaluator = new PredicateEvaluator();
    }

    public void fireEvents(Command command, WorldState worldState, WorldDefinition worldDefinition) {
        GameEvent.Trigger trigger = mapCommandToTrigger(command);
        if (trigger == null) {
            return;
        }

        // Fire events that match this trigger
        for (GameEvent event : worldDefinition.globalEvents()) {
            if (event.trigger() == trigger && event.predicate().test(worldState)) {
                applyActions(event.actions(), worldState);
            }
        }
    }

    private GameEvent.Trigger mapCommandToTrigger(Command command) {
        return switch (command.action()) {
            case Command.ACTION_GO -> GameEvent.Trigger.ENTER_ROOM;
            case Command.ACTION_TAKE -> GameEvent.Trigger.TAKE_ITEM;
            case Command.ACTION_TALK -> GameEvent.Trigger.TALK_NPC;
            default -> null;
        };
    }

    private void applyActions(List<Action> actions, WorldState worldState) {
        for (Action action : actions) {
            applyAction(action, worldState);
        }
    }

    private void applyAction(Action action, WorldState worldState) {
        if (action instanceof Action.SetFlag setFlag) {
            worldState.setFlag(setFlag.flagName(), setFlag.value());
        } else if (action instanceof Action.MoveItem moveItem) {
            worldState.moveItem(moveItem.itemId(), moveItem.fromContainer(), moveItem.toContainer());
        } else if (action instanceof Action.ChangeRoomDescription changeDesc) {
            // This would require updating world definition, which is immutable
            // Skip for now
        } else if (action instanceof Action.EndGame endGame) {
            // Game ending is handled by CommandResult
        }
    }
}
