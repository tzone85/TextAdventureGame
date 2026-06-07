package io.vxd.textadventure.application.parser;

import java.util.Optional;

public class Parser {

    public Command parse(String input) {
        try {
            if (input == null || input.isBlank()) {
                return new Command(Verb.UNKNOWN, Optional.empty(), Optional.empty(), input == null ? "" : "");
            }

            String trimmed = input.trim();
            String[] tokens = trimmed.split("\\s+");

            if (tokens.length == 0) {
                return new Command(Verb.UNKNOWN, Optional.empty(), Optional.empty(), "");
            }

            // Handle direction shortcuts first
            String expandedInput = expandDirectionShortcut(tokens[0], trimmed);
            if (!expandedInput.equals(trimmed)) {
                // Re-parse with expanded direction
                return parse(expandedInput);
            }

            // Try to match multi-word verbs first
            ParseResult result = tryParseMultiWordVerb(tokens, trimmed);
            if (result != null) {
                return result.toCommand(trimmed);
            }

            // Single word verb
            Verb verb = Verb.fromString(tokens[0]);
            if (verb == Verb.UNKNOWN) {
                return new Command(Verb.UNKNOWN, Optional.empty(), Optional.empty(), trimmed);
            }

            // Handle verbs with objects
            if (tokens.length == 1) {
                return new Command(verb, Optional.empty(), Optional.empty(), trimmed);
            }

            // Extract remaining text as objects
            String remaining = trimmed.substring(tokens[0].length()).trim();

            if (verb == Verb.USE) {
                return parseUse(remaining, trimmed);
            }

            if (verb == Verb.PUT) {
                return parsePut(remaining, trimmed);
            }

            if (verb == Verb.TALK) {
                return parseTalk(remaining, trimmed);
            }

            // Default: remaining is direct object
            return new Command(verb, Optional.of(remaining), Optional.empty(), trimmed);

        } catch (Exception e) {
            return new Command(Verb.UNKNOWN, Optional.empty(), Optional.empty(), input == null ? "" : input);
        }
    }

    private String expandDirectionShortcut(String firstToken, String input) {
        String lower = firstToken.toLowerCase();
        String expanded = null;

        switch (lower) {
            case "n":
                expanded = "north";
                break;
            case "s":
                expanded = "south";
                break;
            case "e":
                expanded = "east";
                break;
            case "w":
                expanded = "west";
                break;
            case "north":
            case "south":
            case "east":
            case "west":
            case "up":
            case "down":
                expanded = lower;
                break;
        }

        if (expanded != null) {
            return "go " + expanded;
        }
        return input;
    }

    private ParseResult tryParseMultiWordVerb(String[] tokens, String trimmed) {
        if (tokens.length < 2) {
            return null;
        }

        String firstTwo = (tokens[0] + " " + tokens[1]).toLowerCase();

        if (firstTwo.equals("pick up")) {
            String remaining = tokens.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(tokens, 2, tokens.length)) : "";
            return new ParseResult(Verb.TAKE, remaining.isEmpty() ? null : remaining, null);
        }

        if (firstTwo.equals("look at")) {
            String remaining = tokens.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(tokens, 2, tokens.length)) : "";
            return new ParseResult(Verb.LOOK, remaining.isEmpty() ? null : remaining, null);
        }

        if (firstTwo.equals("talk to")) {
            String remaining = tokens.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(tokens, 2, tokens.length)) : "";
            return new ParseResult(Verb.TALK, remaining.isEmpty() ? null : remaining, null);
        }

        if (firstTwo.equals("look in")) {
            String remaining = tokens.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(tokens, 2, tokens.length)) : "";
            return new ParseResult(Verb.LOOK, remaining.isEmpty() ? null : remaining, null);
        }

        // "use X on Y" - need at least 4 tokens: use, X, on, Y
        if (firstTwo.equals("use") && tokens.length >= 4) {
            // Find "on" to split direct and indirect
            int onIndex = -1;
            for (int i = 2; i < tokens.length; i++) {
                if (tokens[i].toLowerCase().equals("on")) {
                    onIndex = i;
                    break;
                }
            }
            if (onIndex > 2) {
                String directObj = String.join(" ", java.util.Arrays.copyOfRange(tokens, 1, onIndex));
                String indirectObj = String.join(" ", java.util.Arrays.copyOfRange(tokens, onIndex + 1, tokens.length));
                if (!directObj.isEmpty() && !indirectObj.isEmpty()) {
                    return new ParseResult(Verb.USE, directObj, indirectObj);
                }
            }
        }

        // "put X in Y" or "put X on Y"
        if (firstTwo.equals("put") && tokens.length >= 4) {
            // Find "in" or "on"
            int preposIndex = -1;
            for (int i = 2; i < tokens.length; i++) {
                String tok = tokens[i].toLowerCase();
                if (tok.equals("in") || tok.equals("on")) {
                    preposIndex = i;
                    break;
                }
            }
            if (preposIndex > 2) {
                String directObj = String.join(" ", java.util.Arrays.copyOfRange(tokens, 1, preposIndex));
                String indirectObj = String.join(" ", java.util.Arrays.copyOfRange(tokens, preposIndex + 1, tokens.length));
                if (!directObj.isEmpty() && !indirectObj.isEmpty()) {
                    return new ParseResult(Verb.PUT, directObj, indirectObj);
                }
            }
        }

        return null;
    }

    private Command parseUse(String remaining, String raw) {
        if (remaining.isEmpty()) {
            return new Command(Verb.USE, Optional.empty(), Optional.empty(), raw);
        }

        String[] parts = remaining.split("\\s+on\\s+", 2);
        if (parts.length == 2) {
            return new Command(Verb.USE, Optional.of(parts[0]), Optional.of(parts[1]), raw);
        }

        return new Command(Verb.USE, Optional.of(remaining), Optional.empty(), raw);
    }

    private Command parsePut(String remaining, String raw) {
        if (remaining.isEmpty()) {
            return new Command(Verb.PUT, Optional.empty(), Optional.empty(), raw);
        }

        // Try "in" first, then "on"
        String[] parts = remaining.split("\\s+in\\s+", 2);
        if (parts.length == 2) {
            return new Command(Verb.PUT, Optional.of(parts[0]), Optional.of(parts[1]), raw);
        }

        parts = remaining.split("\\s+on\\s+", 2);
        if (parts.length == 2) {
            return new Command(Verb.PUT, Optional.of(parts[0]), Optional.of(parts[1]), raw);
        }

        return new Command(Verb.PUT, Optional.of(remaining), Optional.empty(), raw);
    }

    private Command parseTalk(String remaining, String raw) {
        if (remaining.isEmpty()) {
            return new Command(Verb.TALK, Optional.empty(), Optional.empty(), raw);
        }

        // Remove "to" if present
        if (remaining.toLowerCase().startsWith("to ")) {
            remaining = remaining.substring(3).trim();
        }

        return new Command(Verb.TALK, Optional.of(remaining), Optional.empty(), raw);
    }

    private static class ParseResult {
        Verb verb;
        String directObject;
        String indirectObject;

        ParseResult(Verb verb, String directObject, String indirectObject) {
            this.verb = verb;
            this.directObject = directObject;
            this.indirectObject = indirectObject;
        }

        Command toCommand(String raw) {
            return new Command(
                verb,
                directObject != null && !directObject.isEmpty() ? Optional.of(directObject) : Optional.empty(),
                indirectObject != null && !indirectObject.isEmpty() ? Optional.of(indirectObject) : Optional.empty(),
                raw
            );
        }
    }
}
