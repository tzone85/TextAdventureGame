package io.vxd.textadventure.application;

import io.vxd.textadventure.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventEngineTest {
    private GameEngine gameEngine;
    private WorldState worldState;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngine();

        Item key = new Item("key", List.of("key"), true, false, List.of(), 1);
        ConversationNode npcTree = new ConversationNode("Hi!", Map.of());
        Npc npc = new Npc("npc1", "Guard", "The Guard watches.", npcTree);

        Room room1 = new Room(
            "room1",
            "Start",
            "The starting room.",
            Map.of("north", "room2"),
            List.of("key"),
            List.of("npc1")
        );

        Room room2 = new Room(
            "room2",
            "North Room",
            "A room to the north.",
            Map.of("south", "room1"),
            List.of(),
            List.of()
        );

        worldState = new WorldState(
            "room1",
            Map.of("room1", Set.of("key"), "room2", Set.of(), "inventory", Set.of()),
            Map.of()
        );
    }

    @Test
    void testEventFiresOnTakeItem() {
        Item key = new Item("key", List.of("key"), true, false, List.of(), 1);
        Room room1 = new Room("room1", "Start", "Start room.", Map.of(), List.of("key"), List.of());

        GameEvent event = new GameEvent(
            GameEvent.Trigger.TAKE_ITEM,
            state -> true,
            List.of(new Action.SetFlag("took_key", true))
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room1),
            Map.of("key", key),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(event),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of("key"), "inventory", Set.of()),
            Map.of()
        );

        Command cmd = new Command(Command.ACTION_TAKE, "key");
        gameEngine.tick(cmd, ws, wd);

        assertTrue(ws.getFlag("took_key"));
    }

    @Test
    void testEventFiresOnTalkNpc() {
        ConversationNode tree = new ConversationNode("Hello!", Map.of());
        Npc npc = new Npc("npc1", "Guard", "Guards you", tree);
        Room room1 = new Room("room1", "Start", "Start room.", Map.of(), List.of(), List.of("npc1"));

        GameEvent event = new GameEvent(
            GameEvent.Trigger.TALK_NPC,
            state -> true,
            List.of(new Action.SetFlag("talked_npc", true))
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room1),
            Map.of(),
            Map.of("npc1", npc),
            Map.of(),
            List.of(),
            List.of(event),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of(), "inventory", Set.of()),
            Map.of()
        );

        Command cmd = new Command(Command.ACTION_TALK, "Guard");
        gameEngine.tick(cmd, ws, wd);

        assertTrue(ws.getFlag("talked_npc"));
    }

    @Test
    void testEventFiresOnEnterRoomWithCondition() {
        Room room1 = new Room("room1", "Start", "Start.", Map.of("north", "room2"), List.of(), List.of());
        Room room2 = new Room("room2", "North", "North.", Map.of("south", "room1"), List.of(), List.of());

        GameEvent event = new GameEvent(
            GameEvent.Trigger.ENTER_ROOM,
            state -> state.currentRoomId().equals("room2"),
            List.of(new Action.SetFlag("entered_north", true))
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room1, "room2", room2),
            Map.of(),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(event),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of(), "room2", Set.of(), "inventory", Set.of()),
            Map.of()
        );

        assertFalse(ws.getFlag("entered_north"));
        Command cmd = new Command(Command.ACTION_GO, "north");
        gameEngine.tick(cmd, ws, wd);
        assertTrue(ws.getFlag("entered_north"));
    }

    @Test
    void testEventDoesNotFireWhenPredicateIsFalse() {
        Item key = new Item("key", List.of("key"), true, false, List.of(), 1);
        Room room1 = new Room("room1", "Start", "Start room.", Map.of(), List.of("key"), List.of());

        GameEvent event = new GameEvent(
            GameEvent.Trigger.TAKE_ITEM,
            state -> state.getFlag("has_permission"),
            List.of(new Action.SetFlag("took_with_permission", true))
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room1),
            Map.of("key", key),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(event),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of("key"), "inventory", Set.of()),
            Map.of("has_permission", false)
        );

        Command cmd = new Command(Command.ACTION_TAKE, "key");
        gameEngine.tick(cmd, ws, wd);

        assertFalse(ws.getFlag("took_with_permission"), "Event should not fire when predicate is false");
    }

    @Test
    void testMultipleEventsCanFire() {
        Item key = new Item("key", List.of("key"), true, false, List.of(), 1);
        Room room1 = new Room("room1", "Start", "Start room.", Map.of(), List.of("key"), List.of());

        GameEvent event1 = new GameEvent(
            GameEvent.Trigger.TAKE_ITEM,
            state -> true,
            List.of(new Action.SetFlag("event1_fired", true))
        );

        GameEvent event2 = new GameEvent(
            GameEvent.Trigger.TAKE_ITEM,
            state -> true,
            List.of(new Action.SetFlag("event2_fired", true))
        );

        WorldDefinition wd = new WorldDefinition(
            Map.of("room1", room1),
            Map.of("key", key),
            Map.of(),
            Map.of(),
            List.of(),
            List.of(event1, event2),
            "room1",
            Set.of(),
            Optional.empty()
        );

        WorldState ws = new WorldState(
            "room1",
            Map.of("room1", Set.of("key"), "inventory", Set.of()),
            Map.of()
        );

        Command cmd = new Command(Command.ACTION_TAKE, "key");
        gameEngine.tick(cmd, ws, wd);

        assertTrue(ws.getFlag("event1_fired"));
        assertTrue(ws.getFlag("event2_fired"));
    }

    @Test
    void testPredicateEvaluatorEvaluatesFlagsCorrectly() {
        PredicateEvaluator evaluator = new PredicateEvaluator();

        WorldState ws = new WorldState(
            "room1",
            Map.of("inventory", Set.of()),
            Map.of("test_flag", true, "other_flag", false)
        );

        Predicate truePredicate = state -> state.getFlag("test_flag");
        Predicate falsePredicate = state -> state.getFlag("other_flag");

        assertTrue(evaluator.evaluate(truePredicate, ws));
        assertFalse(evaluator.evaluate(falsePredicate, ws));
    }
}
