package io.vxd.textadventure.domain.world;

import java.util.Collections;
import java.util.Map;

public record Room(
    String id,
    String title,
    String description,
    Map<String, String> exits
) {
    public Room {
        exits = Collections.unmodifiableMap(exits);
    }
}
