package io.vxd.textadventure.infrastructure.persistence;

import io.vxd.textadventure.domain.WorldState;
import io.vxd.textadventure.infrastructure.dto.WorldStateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SaveGameRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void testRoundTripPreservesWorldState() throws Exception {
        // Arrange
        Map<String, Set<String>> itemLocations = new HashMap<>();
        Set<String> room1Items = new HashSet<>();
        room1Items.add("sword");
        itemLocations.put("room1", room1Items);

        Set<String> room2Items = new HashSet<>();
        room2Items.add("key");
        itemLocations.put("room2", room2Items);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("doorOpen", true);
        flags.put("questComplete", false);

        WorldState originalWorldState = new WorldState("startRoom", itemLocations, flags);
        WorldStateDto worldStateDto = WorldStateDto.fromDomain(originalWorldState);

        SaveGame originalSaveGame = new SaveGame(
                SaveGame.FORMAT_VERSION,
                "world123",
                worldStateDto,
                Instant.now()
        );

        SaveGameRepository repository = new SaveGameRepository();
        Path saveFile = tempDir.resolve("save.json");

        // Act
        repository.save(saveFile, originalSaveGame);
        SaveGame loadedSaveGame = repository.load(saveFile);
        WorldState loadedWorldState = loadedSaveGame.state().toDomain();

        // Assert
        assertEquals(originalWorldState.currentRoomId(), loadedWorldState.currentRoomId());
        assertEquals(originalWorldState.itemLocations(), loadedWorldState.itemLocations());
        assertEquals(originalWorldState.flags(), loadedWorldState.flags());
    }

    @Test
    void testIncompatibleVersionThrowsException() throws Exception {
        // Arrange
        Path saveFile = tempDir.resolve("incompatible.json");
        String incompatibleJson = "{"
                + "\"formatVersion\":\"0.9.0\","
                + "\"worldId\":\"test\","
                + "\"state\":{\"currentRoomId\":\"room1\",\"itemLocations\":{},\"flags\":{}},"
                + "\"savedAt\":\"2023-01-01T00:00:00Z\""
                + "}";
        Files.write(saveFile, incompatibleJson.getBytes());

        SaveGameRepository repository = new SaveGameRepository();

        // Act & Assert
        assertThrows(IncompatibleSaveVersionException.class, () -> repository.load(saveFile));
    }

    @Test
    void testSavePersistsAndReloadsWithNewObjectMapper() throws Exception {
        // Arrange
        Map<String, Set<String>> itemLocations = new HashMap<>();
        Set<String> inventoryItems = new HashSet<>();
        inventoryItems.add("potion");
        itemLocations.put("inventory", inventoryItems);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("hasVisitedTown", true);

        WorldState worldState = new WorldState("town", itemLocations, flags);
        WorldStateDto worldStateDto = WorldStateDto.fromDomain(worldState);

        SaveGame originalSaveGame = new SaveGame(
                SaveGame.FORMAT_VERSION,
                "persistent-world",
                worldStateDto,
                Instant.parse("2023-06-07T10:30:00Z")
        );

        Path saveFile = tempDir.resolve("persistent.json");

        // Act - Save with first repository instance
        SaveGameRepository firstRepository = new SaveGameRepository();
        firstRepository.save(saveFile, originalSaveGame);

        // Act - Load with completely new repository instance (simulates new JVM)
        SaveGameRepository secondRepository = new SaveGameRepository();
        SaveGame loadedSaveGame = secondRepository.load(saveFile);

        // Assert
        assertEquals(originalSaveGame.formatVersion(), loadedSaveGame.formatVersion());
        assertEquals(originalSaveGame.worldId(), loadedSaveGame.worldId());
        assertEquals(originalSaveGame.savedAt(), loadedSaveGame.savedAt());

        WorldState loadedWorldState = loadedSaveGame.state().toDomain();
        assertEquals(worldState.currentRoomId(), loadedWorldState.currentRoomId());
        assertEquals(worldState.itemLocations(), loadedWorldState.itemLocations());
        assertEquals(worldState.flags(), loadedWorldState.flags());
    }

    @Test
    void testSaveGameConstantFormatVersion() {
        assertEquals("1.0.0", SaveGame.FORMAT_VERSION);
    }
}