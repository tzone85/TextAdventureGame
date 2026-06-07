package io.vxd.textadventure.ui;

import io.vxd.textadventure.domain.*;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WorldLoader {

    public static WorldDefinition loadWorld(String worldPath) throws IOException {
        String content = Files.readString(Path.of(worldPath));
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(content);

        String startRoomId = (String) data.get("start_room");
        List<String> startInventoryList = (List<String>) data.getOrDefault("start_inventory", List.of());
        Set<String> startInventory = new HashSet<>(startInventoryList);

        Map<String, Room> rooms = loadRooms((Map<String, Object>) data.get("rooms"));
        Map<String, Item> items = loadItems((Map<String, Object>) data.get("items"));
        Map<String, Npc> npcs = loadNpcs((Map<String, Object>) data.getOrDefault("npcs", Map.of()));

        return new WorldDefinition(
            rooms,
            items,
            npcs,
            Map.of(),
            List.of(),
            List.of(),
            startRoomId,
            startInventory,
            Optional.empty()
        );
    }

    private static Map<String, Room> loadRooms(Map<String, Object> roomsData) {
        Map<String, Room> rooms = new HashMap<>();

        if (roomsData == null) return rooms;

        for (String roomId : roomsData.keySet()) {
            Map<String, Object> roomData = (Map<String, Object>) roomsData.get(roomId);
            String title = (String) roomData.get("title");
            String description = (String) roomData.get("description");

            Map<String, String> exits = new HashMap<>();
            Map<String, Object> exitsData = (Map<String, Object>) roomData.getOrDefault("exits", Map.of());
            for (String direction : exitsData.keySet()) {
                exits.put(direction, (String) exitsData.get(direction));
            }

            List<String> itemIds = (List<String>) roomData.getOrDefault("items", List.of());
            List<String> npcIds = (List<String>) roomData.getOrDefault("npcs", List.of());

            Room room = new Room(roomId, title, description, exits, itemIds, npcIds);
            rooms.put(roomId, room);
        }

        return rooms;
    }

    private static Map<String, Item> loadItems(Map<String, Object> itemsData) {
        Map<String, Item> items = new HashMap<>();

        if (itemsData == null) return items;

        for (String itemId : itemsData.keySet()) {
            Map<String, Object> itemData = (Map<String, Object>) itemsData.get(itemId);
            String name = (String) itemData.get("name");
            String description = (String) itemData.getOrDefault("description", "");
            boolean isContainer = (boolean) itemData.getOrDefault("is_container", false);

            Item item = new Item(itemId, List.of(itemId), true, isContainer, List.of(), 1);
            items.put(itemId, item);
        }

        return items;
    }

    private static Map<String, Npc> loadNpcs(Map<String, Object> npcsData) {
        Map<String, Npc> npcs = new HashMap<>();

        if (npcsData == null) return npcs;

        for (String npcId : npcsData.keySet()) {
            Map<String, Object> npcData = (Map<String, Object>) npcsData.get(npcId);
            String name = (String) npcData.get("name");
            String description = (String) npcData.getOrDefault("description", "");

            ConversationNode conversationTree = new ConversationNode(description, Map.of());
            Npc npc = new Npc(npcId, name, description, conversationTree);
            npcs.put(npcId, npc);
        }

        return npcs;
    }
}
