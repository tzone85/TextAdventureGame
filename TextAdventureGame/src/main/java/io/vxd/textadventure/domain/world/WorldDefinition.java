package io.vxd.textadventure.domain.world;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record WorldDefinition(
    String startRoom,
    Map<String, Room> rooms,
    Map<String, Item> items,
    Map<String, Npc> npcs,
    List<WorldEvent> events,
    List<Ending> endings
) {
    public WorldDefinition {
        rooms = Collections.unmodifiableMap(rooms);
        items = Collections.unmodifiableMap(items);
        npcs = Collections.unmodifiableMap(npcs);
        events = Collections.unmodifiableList(events);
        endings = Collections.unmodifiableList(endings);
    }
}
