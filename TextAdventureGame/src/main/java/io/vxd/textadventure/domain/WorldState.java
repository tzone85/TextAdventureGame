package io.vxd.textadventure.domain;

import java.util.*;

/**
 * Mutable world state container for runtime game state management.
 * Provides deep-copy isolation via copy() method.
 */
public final class WorldState {
    private String currentRoomId;
    private final Map<String, Set<String>> itemLocations;
    private final Map<String, Boolean> flags;

    public WorldState(String currentRoomId, Map<String, Set<String>> itemLocations, Map<String, Boolean> flags) {
        Objects.requireNonNull(currentRoomId, "currentRoomId cannot be null");
        Objects.requireNonNull(itemLocations, "itemLocations cannot be null");
        Objects.requireNonNull(flags, "flags cannot be null");

        this.currentRoomId = currentRoomId;
        this.itemLocations = new HashMap<>();
        itemLocations.forEach((key, value) -> this.itemLocations.put(key, new HashSet<>(value)));
        this.flags = new HashMap<>(flags);
    }

    public String currentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String roomId) {
        this.currentRoomId = Objects.requireNonNull(roomId, "roomId cannot be null");
    }

    public Map<String, Set<String>> itemLocations() {
        return itemLocations;
    }

    public void moveItem(String itemId, String fromContainer, String toContainer) {
        Objects.requireNonNull(itemId, "itemId cannot be null");
        Objects.requireNonNull(fromContainer, "fromContainer cannot be null");
        Objects.requireNonNull(toContainer, "toContainer cannot be null");

        removeItem(itemId, fromContainer);
        itemLocations.computeIfAbsent(toContainer, k -> new HashSet<>()).add(itemId);
    }

    public void removeItem(String itemId, String fromContainer) {
        Objects.requireNonNull(itemId, "itemId cannot be null");
        Objects.requireNonNull(fromContainer, "fromContainer cannot be null");

        Set<String> container = itemLocations.get(fromContainer);
        if (container != null) {
            container.remove(itemId);
        }
    }

    public boolean getFlag(String flagName) {
        Objects.requireNonNull(flagName, "flagName cannot be null");
        return flags.getOrDefault(flagName, false);
    }

    public void setFlag(String flagName, boolean value) {
        Objects.requireNonNull(flagName, "flagName cannot be null");
        flags.put(flagName, value);
    }

    public Map<String, Boolean> flags() {
        return new HashMap<>(flags);
    }

    public WorldState copy() {
        Map<String, Set<String>> copiedLocations = new HashMap<>();
        itemLocations.forEach((key, value) -> copiedLocations.put(key, new HashSet<>(value)));

        Map<String, Boolean> copiedFlags = new HashMap<>(flags);

        return new WorldState(currentRoomId, copiedLocations, copiedFlags);
    }
}
