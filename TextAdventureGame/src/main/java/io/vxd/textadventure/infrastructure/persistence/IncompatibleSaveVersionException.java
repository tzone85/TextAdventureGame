package io.vxd.textadventure.infrastructure.persistence;

public class IncompatibleSaveVersionException extends Exception {
    public IncompatibleSaveVersionException(String expectedVersion, String actualVersion) {
        super(String.format("Incompatible save version. Expected: %s, Actual: %s", expectedVersion, actualVersion));
    }
}