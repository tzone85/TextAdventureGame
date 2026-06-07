package io.vxd.textadventure.domain.world;

public record Ending(
    String id,
    EndingType type,
    String message
) {
    public enum EndingType {
        WIN, LOSE
    }
}
