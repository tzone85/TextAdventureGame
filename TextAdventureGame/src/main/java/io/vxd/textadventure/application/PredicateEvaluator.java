package io.vxd.textadventure.application;

import io.vxd.textadventure.domain.*;

public class PredicateEvaluator {
    public boolean evaluate(Predicate predicate, WorldState worldState) {
        return predicate.test(worldState);
    }
}
