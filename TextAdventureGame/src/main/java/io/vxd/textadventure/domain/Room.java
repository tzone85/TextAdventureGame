package io.vxd.textadventure.domain;

import java.util.*;

public record Room(String id, String name, String description, Map<String, String> exits, List<String> itemIds, List<String> npcIds) {
    public Room {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(description, "description cannot be null");
        Objects.requireNonNull(exits, "exits cannot be null");
        Objects.requireNonNull(itemIds, "itemIds cannot be null");
        Objects.requireNonNull(npcIds, "npcIds cannot be null");
        exits = Collections.unmodifiableMap(new HashMap<>(exits));
        itemIds = Collections.unmodifiableList(new ArrayList<>(itemIds));
        npcIds = Collections.unmodifiableList(new ArrayList<>(npcIds));
    }

    @Override
    public Map<String, String> exits() {
        return exits;
    }

    @Override
    public List<String> itemIds() {
        return itemIds;
    }

    @Override
    public List<String> npcIds() {
        return npcIds;
    }
}
