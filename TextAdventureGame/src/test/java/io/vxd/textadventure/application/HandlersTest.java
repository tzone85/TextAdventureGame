package io.vxd.textadventure.application;

import io.vxd.textadventure.application.handlers.*;
import io.vxd.textadventure.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HandlersTest {
    private WorldState worldState;
    private WorldDefinition worldDefinition;
    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngine();

        Item key = new Item("key", List.of("key"), true, false, List.of(), 1);
        Item bag = new Item("bag", List.of("bag"), true, true, List.of(), 2);
        Item book = new Item("book", List.of("book"), false, false, List.of(), 1);

        ConversationNode npcTree = new ConversationNode("Hello there!", Map.of());
        Npc npc = new Npc("npc1", "Elder", "The Elder looks at you.", npcTree);

        Room room1 = new Room(
            "room1",
            "The Starting Room",
            "This is a simple room with a door to the north.",
            Map.of("north", "room2", "east", "room3"),
            List.of("key", "book"),
            List.of("npc1")
        );

        Room room2 = new Room(
            "room2",
            "Northern Room",
            "A dark room to the north.",
            Map.of("south", "room1"),
            List.of(),
            List.of()
        );

        Room room3 = new Room(
            "room3",
            "Eastern Room",
            "A bright room to the east.",
            Map.of("west", "room1"),
            List.of(),
            List.of()
        );

        worldDefinition = new WorldDefinition(
            Map.of("room1", room1, "room2", room2, "room3", room3),
            Map.of("key", key, "bag", bag, "book", book),
            Map.of("npc1", npc),
            Map.of(),
            List.of(),
            List.of(),
            "room1",
            Set.of("bag"),
            Optional.of(10)
        );

        worldState = new WorldState(
            "room1",
            Map.of("room1", Set.of("key", "book"), "room2", Set.of(), "room3", Set.of(), "inventory", Set.of("bag")),
            Map.of()
        );
    }

    @Test
    void testInventoryHandler_ShowsCarriedItems() {
        Command cmd = new Command(Command.ACTION_INVENTORY);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("bag"));
        assertFalse(result.gameOver());
    }

    @Test
    void testPutHandler_MovesItemToContainer() {
        // First, take the key
        gameEngine.tick(new Command(Command.ACTION_TAKE, "key"), worldState, worldDefinition);

        // Then put it in the bag
        Command putCmd = new Command(Command.ACTION_PUT, "key", "bag");
        CommandResult result = gameEngine.tick(putCmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(worldState.itemLocations().get("bag").contains("key"));
    }

    @Test
    void testLookInContainer_ShowsNestedItems() {
        // Take key and put in bag
        gameEngine.tick(new Command(Command.ACTION_TAKE, "key"), worldState, worldDefinition);
        gameEngine.tick(new Command(Command.ACTION_PUT, "key", "bag"), worldState, worldDefinition);

        // Look in bag
        Command cmd = new Command(Command.ACTION_LOOK, "bag");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("key"));
    }

    @Test
    void testTalkHandler_ReturnsNpcDialogue() {
        Command cmd = new Command(Command.ACTION_TALK, "Elder");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().contains("Hello there!"));
    }

    @Test
    void testGoHandler_FailsOnNonExistentExit() {
        Command cmd = new Command(Command.ACTION_GO, "west");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertEquals("room1", worldState.currentRoomId(), "Should not move to non-existent exit");
        assertTrue(result.output().toLowerCase().contains("cannot"));
    }

    @Test
    void testGoHandler_SucceedsOnValidExit() {
        Command cmd = new Command(Command.ACTION_GO, "north");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertEquals("room2", worldState.currentRoomId());
    }

    @Test
    void testLookHandler_ShowsRoomDescription() {
        Command cmd = new Command(Command.ACTION_LOOK);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().contains("Starting Room"));
    }

    @Test
    void testLookHandler_ShowsItems() {
        Command cmd = new Command(Command.ACTION_LOOK);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("key"));
    }

    @Test
    void testLookHandler_ShowsExits() {
        Command cmd = new Command(Command.ACTION_LOOK);
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertTrue(result.output().toLowerCase().contains("north") || result.output().toLowerCase().contains("exit"));
    }

    @Test
    void testTakeHandler_RespectsTakeableFlag() {
        Command cmd = new Command(Command.ACTION_TAKE, "book");
        CommandResult result = gameEngine.tick(cmd, worldState, worldDefinition);

        assertNotNull(result);
        assertFalse(worldState.itemLocations().get("inventory").contains("book"));
        assertTrue(result.output().toLowerCase().contains("cannot"));
    }

    @Test
    void testEventEngine_FiresOnEnterRoom() {
        GameEvent event = new GameEvent(
            GameEvent.Trigger.ENTER_ROOM,
            state -> state.currentRoomId().equals("room2"),
            List.of(new Action.SetFlag("north_visited", true))
        );

        WorldDefinition wd = new WorldDefinition(
            worldDefinition.rooms(),
            worldDefinition.items(),
            worldDefinition.npcs(),
            worldDefinition.events(),
            worldDefinition.endings(),
            List.of(event),
            worldDefinition.startRoomId(),
            worldDefinition.startInventory(),
            worldDefinition.inventoryWeightLimit()
        );

        Command cmd = new Command(Command.ACTION_GO, "north");
        gameEngine.tick(cmd, worldState, wd);

        assertTrue(worldState.getFlag("north_visited"));
    }

    @Test
    void testWeightLimit_PreventsOvertakingItems() {
        Item heavyItem = new Item("stone", List.of("stone"), true, false, List.of(), 15);
        Room room = new Room(
            "room1",
            "Test Room",
            "Test",
            Map.of(),
            List.of("stone"),
            List.of()
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room),
            Map.of("stone", heavyItem),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(),
            "room1",
            Set.of(),
            Optional.of(10)
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of("stone"), "inventory", Set.of()),
            Map.of()
        );

        Command cmd = new Command(Command.ACTION_TAKE, "stone");
        CommandResult result = gameEngine.tick(cmd, ws, wd);

        assertNotNull(result);
        assertFalse(ws.itemLocations().get("inventory").contains("stone"));
    }
}
