package io.vxd.textadventure.domain.world;

import java.util.Collections;
import java.util.List;

public record WorldEvent(
    String id,
    String trigger,
    List<Predicate> predicates,
    String action
) {
    public WorldEvent {
        predicates = Collections.unmodifiableList(predicates);
    }
}
