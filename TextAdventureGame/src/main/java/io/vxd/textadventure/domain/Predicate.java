package io.vxd.textadventure.domain;

/**
 * Functional interface for evaluating conditions against WorldState.
 */
@FunctionalInterface
public interface Predicate {
    boolean test(WorldState state);
}
