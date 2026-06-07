package io.vxd.textadventure.domain;

import java.util.*;

public record WorldDefinition(
    Map<String, Room> rooms,
    Map<String, Item> items,
    Map<String, Npc> npcs,
    Map<String, GameEvent> events,
    List<Ending> endings,
    List<GameEvent> globalEvents,
    String startRoomId,
    Set<String> startInventory,
    Optional<Integer> inventoryWeightLimit
) {
    public WorldDefinition {
        Objects.requireNonNull(rooms, "rooms cannot be null");
        Objects.requireNonNull(items, "items cannot be null");
        Objects.requireNonNull(npcs, "npcs cannot be null");
        Objects.requireNonNull(events, "events cannot be null");
        Objects.requireNonNull(endings, "endings cannot be null");
        Objects.requireNonNull(globalEvents, "globalEvents cannot be null");
        Objects.requireNonNull(startRoomId, "startRoomId cannot be null");
        Objects.requireNonNull(startInventory, "startInventory cannot be null");
        Objects.requireNonNull(inventoryWeightLimit, "inventoryWeightLimit cannot be null");

        rooms = Collections.unmodifiableMap(new HashMap<>(rooms));
        items = Collections.unmodifiableMap(new HashMap<>(items));
        npcs = Collections.unmodifiableMap(new HashMap<>(npcs));
        events = Collections.unmodifiableMap(new HashMap<>(events));
        endings = Collections.unmodifiableList(new ArrayList<>(endings));
        globalEvents = Collections.unmodifiableList(new ArrayList<>(globalEvents));
        startInventory = Collections.unmodifiableSet(new HashSet<>(startInventory));
    }

    @Override
    public Map<String, Room> rooms() {
        return rooms;
    }

    @Override
    public Map<String, Item> items() {
        return items;
    }

    @Override
    public Map<String, Npc> npcs() {
        return npcs;
    }

    @Override
    public Map<String, GameEvent> events() {
        return events;
    }

    @Override
    public List<Ending> endings() {
        return endings;
    }

    @Override
    public List<GameEvent> globalEvents() {
        return globalEvents;
    }

    @Override
    public Set<String> startInventory() {
        return startInventory;
    }
}
