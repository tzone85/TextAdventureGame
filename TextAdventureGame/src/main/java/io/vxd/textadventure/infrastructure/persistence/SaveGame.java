package io.vxd.textadventure.infrastructure.persistence;

import io.vxd.textadventure.infrastructure.dto.WorldStateDto;
import java.time.Instant;

public record SaveGame(
    String formatVersion,
    String worldId,
    WorldStateDto state,
    Instant savedAt
) {
    public static final String FORMAT_VERSION = "1.0.0";
}