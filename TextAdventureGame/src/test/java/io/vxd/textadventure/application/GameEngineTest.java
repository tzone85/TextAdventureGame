package io.vxd.textadventure.application;

import io.vxd.textadventure.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {
    private GameEngine gameEngine;
    private WorldState worldState;
    private WorldDefinition worldDefinition;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngine();

        // Create a simple room
        Room startRoom = new Room(
            "room1",
            "Start Room",
            "You are in the start room.",
            Map.of("north", "room2"),
            List.of("item1"),
            List.of()
        );

        Room northRoom = new Room(
            "room2",
            "North Room",
            "You are in the north room.",
            Map.of("south", "room1"),
            List.of(),
            List.of()
        );

        // Create items
        Item item1 = new Item("item1", List.of("key"), true, false, List.of(), 1);
        Item bag = new Item("bag", List.of("bag"), true, true, List.of(), 2);

        // Create world definition
        worldDefinition = new WorldDefinition(
            Map.of("room1", startRoom, "room2", northRoom),
            Map.of("item1", item1, "bag", bag),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(),
            "room1",
            Set.of("bag"),
            Optional.of(10)
        );

        // Create world state
        worldState = new WorldState(
            "room1",
            Map.of("room1", Set.of("item1"), "inventory", Set.of("bag")),
            Map.of()
        );
    }

    @Test
    void testTakeMovesItemFromRoomToInventory() {
        Command cmd = new Command(Command.ACTION_TAKE, "key");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertFalse(result.gameOver());
        assertTrue(worldState.itemLocations().get("inventory").contains("item1"));
    }

    @Test
    void testTakeNonTakeableItemRejected() {
        Item nonTakeable = new Item("book", List.of("book"), false, false, List.of(), 1);
        Room room = new Room(
            "room1",
            "Start Room",
            "You are in the start room.",
            Map.of(),
            List.of("book"),
            List.of()
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room),
            Map.of("book", nonTakeable),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of("book"), "inventory", Set.of()),
            Map.of()
        );

        Command cmd = new Command(Command.ACTION_TAKE, "book");
        CommandResult result = gameEngine.tick(cmd, ws, wd);

        assertNotNull(result);
        assertFalse(ws.itemLocations().get("inventory").contains("book"));
    }

    @Test
    void testGoBlockedByMissingExit() {
        Command cmd = new Command(Command.ACTION_GO, "east");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertEquals("room1", worldState.currentRoomId());
    }

    @Test
    void testGoMovesToValidExit() {
        Command cmd = new Command(Command.ACTION_GO, "north");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertEquals("room2", worldState.currentRoomId());
    }

    @Test
    void testLookShowsRoomDescription() {
        Command cmd = new Command(Command.ACTION_LOOK);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().contains("Start Room"));
    }

    @Test
    void testLookShowsVisibleItems() {
        Command cmd = new Command(Command.ACTION_LOOK);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("key"));
    }

    @Test
    void testInventoryShowsHeldItems() {
        Command cmd = new Command(Command.ACTION_INVENTORY);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("bag"));
    }

    @Test
    void testContainerNesting_LookInContainer() {
        // Put key in bag
        worldState.moveItem("item1", "room1", "bag");

        Command cmd = new Command(Command.ACTION_LOOK, "bag");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("key"));
    }

    @Test
    void testDisambiguationWhenMultipleItemsMatch() {
        Item key1 = new Item("key1", List.of("key"), true, false, List.of(), 1);
        Item key2 = new Item("key2", List.of("key"), true, false, List.of(), 1);
        Room room = new Room(
            "room1",
            "Start Room",
            "You are in the start room.",
            Map.of(),
            List.of("key1", "key2"),
            List.of()
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room),
            Map.of("key1", key1, "key2", key2),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of("key1", "key2"), "inventory", Set.of()),
            Map.of()
        );

        Command cmd = new Command(Command.ACTION_TAKE, "key");
        CommandResult result = gameEngine.tick(cmd, ws, wd);

        assertNotNull(result);
        assertTrue(result.disambiguation().isPresent(), "Disambiguation should be present");
    }

    @Test
    void testEventFiresOnEnterRoom() {
        GameEvent event = new GameEvent(
            GameEvent.Trigger.ENTER_ROOM,
            state -> state.currentRoomId().equals("room2"),
            List.of(new Action.SetFlag("visited_north", true))
        );

        Room room2 = new Room(
            "room2",
            "North Room",
            "You are in the north room.",
            Map.of("south", "room1"),
            List.of(),
            List.of()
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", worldDefinition.rooms().get("room1"), "room2", room2),
            worldDefinition.items(),
            worldDefinition.npcs(),
            Map.of("event1", event),
            worldDefinition.endings(),
            List.of(event),
            "room1",
            worldDefinition.startInventory(),
            worldDefinition.inventoryWeightLimit()
        );

        Command cmd = new Command(Command.ACTION_GO, "north");
        CommandResult result = gameEngine.tick(cmd, worldState, wd);

        assertTrue(worldState.getFlag("visited_north"));
    }

    @Test
    void testEndingTriggers() {
        worldState.setFlag("has_key", true);

        Ending ending = new Ending(
            "happy_end",
            state -> state.getFlag("has_key"),
            true,
            "You escaped with the key!"
        );

        WorldDefinition wd = new WorldDefinition(
            worldDefinition.rooms(),
            worldDefinition.items(),
            worldDefinition.npcs(),
            worldDefinition.events(),
            List.of(ending),
            worldDefinition.globalEvents(),
            worldDefinition.startRoomId(),
            worldDefinition.startInventory(),
            worldDefinition.inventoryWeightLimit()
        );

        Command cmd = new Command(Command.ACTION_LOOK);
        CommandResult result = gameEngine.tick(cmd, worldState, wd);

        assertTrue(result.gameOver());
        assertTrue(result.endingId().isPresent());
        assertEquals("happy_end", result.endingId().get());
    }
}
