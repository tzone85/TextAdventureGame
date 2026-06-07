package io.vxd.textadventure.application;

import java.util.*;

public record CommandResult(String output, boolean gameOver, Optional<String> endingId, Optional<String> disambiguation) {
    public CommandResult {
        Objects.requireNonNull(output, "output cannot be null");
        Objects.requireNonNull(endingId, "endingId cannot be null");
        Objects.requireNonNull(disambiguation, "disambiguation cannot be null");
    }

    public CommandResult(String output) {
        this(output, false, Optional.empty(), Optional.empty());
    }

    public CommandResult(String output, boolean gameOver, String endingId) {
        this(output, gameOver, Optional.of(endingId), Optional.empty());
    }

    public static CommandResult withDisambiguation(String output) {
        return new CommandResult(output, false, Optional.empty(), Optional.of(output));
    }
}
