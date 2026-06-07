package io.vxd.textadventure.domain.world;

public record Item(
    String id,
    String name,
    String description,
    boolean isContainer
) {
}
