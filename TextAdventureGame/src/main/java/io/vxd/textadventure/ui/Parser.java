package io.vxd.textadventure.ui;

import io.vxd.textadventure.application.Command;
import java.util.Arrays;

public class Parser {

    public Command parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Command("LOOK");
        }

        String[] parts = input.trim().split("\\s+");
        String action = parts[0].toUpperCase();

        return switch (action) {
            case "LOOK" -> new Command("LOOK");
            case "INVENTORY" -> new Command("INVENTORY");
            case "SAVE" -> new Command("SAVE");
            case "LOAD" -> new Command("LOAD");
            case "QUIT" -> new Command("QUIT");
            case "TAKE" -> parseTake(parts);
            case "GO" -> parseGo(parts);
            case "USE" -> parseUse(parts);
            case "TALK" -> parseTalk(parts);
            case "PUT" -> parsePut(parts);
            default -> new Command(action);
        };
    }

    private Command parseTake(String[] parts) {
        if (parts.length < 2) {
            return new Command("TAKE");
        }
        String target = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        return new Command("TAKE", target);
    }

    private Command parseGo(String[] parts) {
        if (parts.length < 2) {
            return new Command("GO");
        }
        String target = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        return new Command("GO", target);
    }

    private Command parseUse(String[] parts) {
        if (parts.length < 2) {
            return new Command("USE");
        }
        String target = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        return new Command("USE", target);
    }

    private Command parseTalk(String[] parts) {
        if (parts.length < 2) {
            return new Command("TALK");
        }
        String target = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        return new Command("TALK", target);
    }

    private Command parsePut(String[] parts) {
        if (parts.length < 2) {
            return new Command("PUT");
        }

        // Look for "in" keyword
        int inIndex = -1;
        for (int i = 1; i < parts.length; i++) {
            if ("in".equalsIgnoreCase(parts[i])) {
                inIndex = i;
                break;
            }
        }

        if (inIndex == -1) {
            // No "in" found, treat everything as target
            String target = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            return new Command("PUT", target);
        }

        String target = String.join(" ", Arrays.copyOfRange(parts, 1, inIndex));
        String container = String.join(" ", Arrays.copyOfRange(parts, inIndex + 1, parts.length));

        if (container.isEmpty()) {
            return new Command("PUT", target);
        }

        return new Command("PUT", target, container);
    }
}
