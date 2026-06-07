package io.vxd.textadventure.domain;

import java.util.*;

public record GameEvent(Trigger trigger, Predicate predicate, List<Action> actions) {
    public enum Trigger {
        ENTER_ROOM,
        TAKE_ITEM,
        TALK_NPC,
        FLAG_CHANGE
    }

    public GameEvent {
        Objects.requireNonNull(trigger, "trigger cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");
        Objects.requireNonNull(actions, "actions cannot be null");
        actions = Collections.unmodifiableList(new ArrayList<>(actions));
    }

    @Override
    public List<Action> actions() {
        return actions;
    }
}
