package io.vxd.textadventure.application.parser;

import java.util.HashMap;
import java.util.Map;

public enum Verb {
    LOOK, EXAMINE, TAKE, GO, INVENTORY, USE, TALK, PUT, SAVE, LOAD, QUIT, UNKNOWN;

    private static final Map<String, Verb> SYNONYM_MAP = new HashMap<>();

    static {
        SYNONYM_MAP.put("look", LOOK);
        SYNONYM_MAP.put("l", LOOK);
        SYNONYM_MAP.put("examine", LOOK);
        SYNONYM_MAP.put("look at", LOOK);

        SYNONYM_MAP.put("take", TAKE);
        SYNONYM_MAP.put("get", TAKE);
        SYNONYM_MAP.put("pick up", TAKE);

        SYNONYM_MAP.put("go", GO);
        SYNONYM_MAP.put("move", GO);
        SYNONYM_MAP.put("n", GO);
        SYNONYM_MAP.put("s", GO);
        SYNONYM_MAP.put("e", GO);
        SYNONYM_MAP.put("w", GO);
        SYNONYM_MAP.put("north", GO);
        SYNONYM_MAP.put("south", GO);
        SYNONYM_MAP.put("east", GO);
        SYNONYM_MAP.put("west", GO);
        SYNONYM_MAP.put("up", GO);
        SYNONYM_MAP.put("down", GO);

        SYNONYM_MAP.put("inventory", INVENTORY);
        SYNONYM_MAP.put("i", INVENTORY);
        SYNONYM_MAP.put("inv", INVENTORY);

        SYNONYM_MAP.put("use", USE);
        SYNONYM_MAP.put("talk", TALK);
        SYNONYM_MAP.put("talk to", TALK);
        SYNONYM_MAP.put("put", PUT);
        SYNONYM_MAP.put("save", SAVE);
        SYNONYM_MAP.put("load", LOAD);
        SYNONYM_MAP.put("quit", QUIT);
        SYNONYM_MAP.put("exit", QUIT);
    }

    public static Verb fromString(String input) {
        if (input == null || input.isEmpty()) {
            return UNKNOWN;
        }

        String lower = input.toLowerCase().trim();
        return SYNONYM_MAP.getOrDefault(lower, UNKNOWN);
    }
}
