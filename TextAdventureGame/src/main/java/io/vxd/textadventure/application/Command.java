package io.vxd.textadventure.application;

import java.util.*;

public record Command(String action, String target, String container) {
    public static final String ACTION_LOOK = "LOOK";
    public static final String ACTION_TAKE = "TAKE";
    public static final String ACTION_GO = "GO";
    public static final String ACTION_INVENTORY = "INVENTORY";
    public static final String ACTION_USE = "USE";
    public static final String ACTION_TALK = "TALK";
    public static final String ACTION_PUT = "PUT";

    public Command {
        Objects.requireNonNull(action, "action cannot be null");
    }

    public Command(String action) {
        this(action, null, null);
    }

    public Command(String action, String target) {
        this(action, target, null);
    }
}
