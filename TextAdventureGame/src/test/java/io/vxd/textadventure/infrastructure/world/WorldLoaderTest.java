package io.vxd.textadventure.infrastructure.world;

import io.vxd.textadventure.domain.world.Ending;
import io.vxd.textadventure.domain.world.WorldDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WorldLoaderTest {

    @Test
    @DisplayName("should load valid manor.yaml world")
    void testLoadValidWorld() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertNotNull(world);
        assertEquals("foyer", world.startRoom());
        assertFalse(world.rooms().isEmpty());
    }

    @Test
    @DisplayName("should have at least 3 rooms in manor.yaml")
    void testManorHasMinimumRooms() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertTrue(world.rooms().size() >= 3,
            "Manor should have at least 3 rooms, but has " + world.rooms().size());
    }

    @Test
    @DisplayName("should have at least 4 items in manor.yaml")
    void testManorHasMinimumItems() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertTrue(world.items().size() >= 4,
            "Manor should have at least 4 items, but has " + world.items().size());
    }

    @Test
    @DisplayName("should have at least 2 win endings and 1 lose ending in manor.yaml")
    void testManorHasRequiredEndings() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        long winCount = world.endings().stream()
            .filter(e -> e.type() == Ending.EndingType.WIN)
            .count();
        long loseCount = world.endings().stream()
            .filter(e -> e.type() == Ending.EndingType.LOSE)
            .count();

        assertTrue(winCount >= 2, "Manor should have at least 2 win endings, but has " + winCount);
        assertTrue(loseCount >= 1, "Manor should have at least 1 lose ending, but has " + loseCount);
    }

    @Test
    @DisplayName("should reject world with missing exit target room")
    void testRejectMissingExitTarget() {
        var roomDtos = new java.util.HashMap<String, YamlSchema.RoomDto>();
        var exits = new java.util.HashMap<String, String>();
        exits.put("north", "nonexistent_room");
        roomDtos.put("room1", new YamlSchema.RoomDto("room1", "Room 1", "A test room", exits));

        var endingDtos = java.util.List.of(
            new YamlSchema.EndingDto("end1", "WIN", "You won!")
        );

        YamlSchema.WorldDto dto = new YamlSchema.WorldDto(
            "room1", null, roomDtos, new java.util.HashMap<>(),
            new java.util.HashMap<>(), java.util.List.of(), endingDtos
        );

        WorldValidationException exception = assertThrows(WorldValidationException.class, () ->
            WorldMapper.mapToWorldDefinition(dto)
        );

        assertTrue(exception.getMessage().contains("exit"));
    }

    @Test
    @DisplayName("should reject world with duplicate item ids")
    void testRejectDuplicateItemIds() {
        WorldValidationException exception = assertThrows(WorldValidationException.class, () -> {
            var itemDtos = new java.util.HashMap<String, YamlSchema.ItemDto>();
            itemDtos.put("key", new YamlSchema.ItemDto("key", "Key", "A gold key", false));
            itemDtos.put("key2", new YamlSchema.ItemDto("key", "Key", "A gold key", false));

            var roomDtos = new java.util.HashMap<String, YamlSchema.RoomDto>();
            roomDtos.put("foyer", new YamlSchema.RoomDto("foyer", "Foyer", "Entrance hall", new java.util.HashMap<>()));

            var endingDtos = java.util.List.of(
                new YamlSchema.EndingDto("win", "WIN", "You won!")
            );

            YamlSchema.WorldDto dto = new YamlSchema.WorldDto(
                "foyer", null, roomDtos, itemDtos, new java.util.HashMap<>(), java.util.List.of(), endingDtos
            );

            WorldMapper.mapToWorldDefinition(dto);
        });

        assertTrue(exception.getMessage().contains("duplicate item id"));
    }

    @Test
    @DisplayName("should reject world with missing start_room")
    void testRejectMissingStartRoom() {
        var roomDtos = new java.util.HashMap<String, YamlSchema.RoomDto>();
        roomDtos.put("foyer", new YamlSchema.RoomDto("foyer", "Foyer", "Entrance hall", new java.util.HashMap<>()));

        var endingDtos = java.util.List.of(
            new YamlSchema.EndingDto("win", "WIN", "You won!")
        );

        YamlSchema.WorldDto dto = new YamlSchema.WorldDto(
            "nonexistent_room", null, roomDtos, new java.util.HashMap<>(),
            new java.util.HashMap<>(), java.util.List.of(), endingDtos
        );

        WorldValidationException exception = assertThrows(WorldValidationException.class, () ->
            WorldMapper.mapToWorldDefinition(dto)
        );

        assertTrue(exception.getMessage().contains("start_room"));
    }

    @Test
    @DisplayName("should reject world with no endings")
    void testRejectNoEndings() {
        var roomDtos = new java.util.HashMap<String, YamlSchema.RoomDto>();
        roomDtos.put("foyer", new YamlSchema.RoomDto("foyer", "Foyer", "Entrance hall", new java.util.HashMap<>()));

        YamlSchema.WorldDto dto = new YamlSchema.WorldDto(
            "foyer", null, roomDtos, new java.util.HashMap<>(),
            new java.util.HashMap<>(), java.util.List.of(), java.util.List.of()
        );

        WorldValidationException exception = assertThrows(WorldValidationException.class, () ->
            WorldMapper.mapToWorldDefinition(dto)
        );

        assertTrue(exception.getMessage().contains("ending"));
    }

    @Test
    @DisplayName("should have at least 1 bag container item in manor.yaml")
    void testManorHasBagContainer() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertTrue(world.items().values().stream()
            .anyMatch(item -> item.id().equalsIgnoreCase("bag") && item.isContainer()),
            "Manor should have a BAG container item");
    }

    @Test
    @DisplayName("should have a key item in manor.yaml")
    void testManorHasKey() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertTrue(world.items().values().stream()
            .anyMatch(item -> item.id().equalsIgnoreCase("key")),
            "Manor should have a KEY item");
    }

    @Test
    @DisplayName("should have at least 1 NPC in manor.yaml")
    void testManorHasNpc() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertTrue(world.npcs().size() >= 1,
            "Manor should have at least 1 NPC");
    }

    @Test
    @DisplayName("start_room should be foyer in manor.yaml")
    void testManorStartRoomIsFoyer() {
        WorldDefinition world = WorldLoader.load("worlds/manor.yaml");

        assertEquals("foyer", world.startRoom());
    }
}
