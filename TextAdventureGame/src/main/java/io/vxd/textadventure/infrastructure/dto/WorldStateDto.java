package io.vxd.textadventure.infrastructure.dto;

import io.vxd.textadventure.domain.WorldState;
import java.util.Map;
import java.util.Set;

public class WorldStateDto {
    private String currentRoomId;
    private Map<String, Set<String>> itemLocations;
    private Map<String, Boolean> flags;

    public WorldStateDto() {
        // Default constructor for Jackson
    }

    public WorldStateDto(String currentRoomId, Map<String, Set<String>> itemLocations, Map<String, Boolean> flags) {
        this.currentRoomId = currentRoomId;
        this.itemLocations = itemLocations;
        this.flags = flags;
    }

    public static WorldStateDto fromDomain(WorldState worldState) {
        return new WorldStateDto(
                worldState.currentRoomId(),
                worldState.itemLocations(),
                worldState.flags()
        );
    }

    public WorldState toDomain() {
        return new WorldState(currentRoomId, itemLocations, flags);
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public Map<String, Set<String>> getItemLocations() {
        return itemLocations;
    }

    public void setItemLocations(Map<String, Set<String>> itemLocations) {
        this.itemLocations = itemLocations;
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }
}