package io.vxd.textadventure.domain;

import java.util.*;

public record Ending(String id, Predicate predicate, boolean isWin, String epilogue) {
    public Ending {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");
        Objects.requireNonNull(epilogue, "epilogue cannot be null");
    }
}
